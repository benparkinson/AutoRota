package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.OnlyOneShiftADayRule;
import com.parkinsonhardy.autorota.rules.ShiftBlocksSoftRule;
import com.parkinsonhardy.autorota.rules.SoftRule;
import org.joda.time.DateTime;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerRotaEngine extends RotaEngine {

    public PlannerRotaEngine() {
        super();
        addRule(new OnlyOneShiftADayRule());
    }

    @Override
    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        runPreChecks(from, to);

        List<Shift> shifts = createShifts(from, to);

        RotaSolution rotaSolution = new RotaSolution(employees, shifts, rules, softRules);

        List<ShiftBlock> shiftBlocks = new ArrayList<>();
        for (SoftRule softRule : softRules) {
            if (softRule instanceof ShiftBlocksSoftRule) {
                ShiftBlock preferredShiftBlock = ((ShiftBlocksSoftRule) softRule).getPreferredShiftBlock();
                shiftBlocks.add(preferredShiftBlock);
            }
        }

        SolverFactory<RotaSolution> solverFactory = createSolverFactory();

        Solver<RotaSolution> rotaSolutionSolver = solverFactory.buildSolver();

        RotaSolution solve = rotaSolutionSolver.solve(rotaSolution);

        if (!solve.getScore().isFeasible()) {
            throw new RotaException("No feasible solution found within time constraints!");
        }

        HardSoftScore score = solve.getScore();
        System.out.println(String.format("Score: Hard: %s, Soft: %s", score.getHardScore(), score.getSoftScore()));

        List<Shift> assignedShifts = solve.getShifts();
        Map<Employee, List<Shift>> shiftsByEmployee = new HashMap<>();

        for (Shift shift : assignedShifts) {
            if (shift.getEmployee() != null) {
                List<Shift> shiftsForEmployee = shiftsByEmployee.computeIfAbsent(shift.getEmployee(), k -> new ArrayList<>());
                shiftsForEmployee.add(shift);
            }
        }

        for (Map.Entry<Employee, List<Shift>> shiftsForEmployee : shiftsByEmployee.entrySet()) {
            shiftsForEmployee.getValue().sort(Shift::compareTo);

            for (Shift shift : shiftsForEmployee.getValue()) {
                shiftsForEmployee.getKey().addShift(shift);
            }
        }
    }

    protected SolverFactory<RotaSolution> createSolverFactory() {
        SolverFactory<RotaSolution> solverFactory = SolverFactory.createFromXmlResource("rotaSolutionSolverConfig.xml");
        TerminationConfig terminationConfig = solverFactory.getSolverConfig().getTerminationConfig();
        terminationConfig.setSecondsSpentLimit((long) timeoutInSeconds);
        return solverFactory;
    }
}
