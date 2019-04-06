package com.parkinsonhardy.autorota.engine.planner;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.*;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.OnlyOneShiftADayRule;
import com.parkinsonhardy.autorota.rules.ShiftBlocksSoftRule;
import org.joda.time.DateTime;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class PlannerRotaEngine extends RotaEngine {

    private static final Logger logger = LoggerFactory.getLogger(PlannerRotaEngine.class);

    public PlannerRotaEngine() {
        addRule(new OnlyOneShiftADayRule());
    }

    @Override
    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        runPreChecks(from, to);

        List<ShiftBlock> shiftBlocks = getMandatoryShiftBlocks();

        List<Shift> shifts = createShifts(from, to);
        List<ShiftGroup> shiftGroups = createShiftGroups(shifts, shiftBlocks);

        RotaSolution input = new RotaSolution(employees, shiftGroups, rules, softRules);
        RotaSolution solve = createRota(input);

        HardSoftScore score = solve.getScore();
        logger.info(String.format("Score: Hard: %s, Soft: %s", score.getHardScore(), score.getSoftScore()));

        assignShiftsToEmployees(solve);
    }

    private List<ShiftBlock> getMandatoryShiftBlocks() {
        return softRules.stream()
                .filter(rule -> rule.getRuleType() == RuleType.ShiftBlocks)
                .filter(rule -> ((ShiftBlocksSoftRule) rule).isMandatory())
                .map(shiftBlockRule -> ((ShiftBlocksSoftRule) shiftBlockRule).getPreferredShiftBlock())
                .collect(Collectors.toList());
    }

    private List<ShiftGroup> createShiftGroups(List<Shift> shifts, List<ShiftBlock> shiftBlocks) {
        Set<Shift> singleShifts = new HashSet<>(shifts);
        Map<DateTime, Map<ShiftBlock, ShiftBlockInstance>> shiftBlockInstances = new HashMap<>();

        for (Shift shift : shifts) {
            ShiftBlockInstance shiftBlockInstance = getCorrespondingShiftBlock(shift, shiftBlocks, shiftBlockInstances);
            if (shiftBlockInstance != null) {
                shiftBlockInstance.addShift(shift);
                singleShifts.remove(shift);
            }
        }

        List<ShiftBlockInstance> flattenedShiftBlocks = new ArrayList<>();
        for (Map<ShiftBlock, ShiftBlockInstance> entry : shiftBlockInstances.values()) {
            flattenedShiftBlocks.addAll(entry.values());
        }

        List<ShiftGroup> shiftGroups = new ArrayList<>();
        for (ShiftBlockInstance groupedShifts : flattenedShiftBlocks) {
            for (List<Shift> shiftsInBlock : groupedShifts.getAllShifts()) {
                shiftGroups.add(new ShiftGroup(shiftsInBlock));
            }
        }

        for (Shift shift : singleShifts) {
            shiftGroups.add(new ShiftGroup(shift));
        }

        shiftGroups.sort(ShiftGroup::compareTo);

        return shiftGroups;
    }

    private ShiftBlockInstance getCorrespondingShiftBlock(Shift shift, List<ShiftBlock> shiftBlocks,
                                                          Map<DateTime, Map<ShiftBlock, ShiftBlockInstance>> shiftBlockInstances) {
        for (ShiftBlock shiftBlock : shiftBlocks) {
            if (!shift.getShiftType().equals(shiftBlock.getShiftType())) {
                continue;
            }

            DayOfWeek shiftDay = DayOfWeek.of(shift.getStartTime().getDayOfWeek());
            if (!shiftBlock.isForDay(shiftDay)) {
                continue;
            }

            DateTime start;
            if (!shiftBlock.isForPreviousDay(shiftDay)) {
                // first day of shift block
                start = shift.getStartTime();
            } else {
                start = getStartOfShiftBlock(shift, shiftBlock);
            }

            Map<ShiftBlock, ShiftBlockInstance> shiftBlockMap =
                    shiftBlockInstances.computeIfAbsent(start, k -> new HashMap<>());
            return shiftBlockMap.computeIfAbsent(shiftBlock, k -> new ShiftBlockInstance(shiftBlock, start));
        }

        return null;
    }

    private DateTime getStartOfShiftBlock(Shift shift, ShiftBlock shiftBlock) {
        // find start time of first shift in block
        boolean foundFirstDay = false;
        DateTime start = shift.getStartTime().minusDays(1);
        // this while loop prevents us from supporting shift blocks for every day of week (but that's a bit of a weird case)
        while (!foundFirstDay) {
            if (!shiftBlock.isForPreviousDay(DayOfWeek.of(start.getDayOfWeek()))) {
                foundFirstDay = true;
            } else {
                start = start.minusDays(1);
            }
        }
        return start;
    }

    private RotaSolution createRota(RotaSolution rotaSolution) throws RotaException {
        SolverFactory<RotaSolution> solverFactory = createSolverFactory();

        Solver<RotaSolution> rotaSolutionSolver = solverFactory.buildSolver();

        RotaSolution solve = rotaSolutionSolver.solve(rotaSolution);

        if (!solve.getScore().isFeasible()) {
            throw new RotaException("No feasible solution found within time constraints!");
        }
        return solve;
    }

    protected SolverFactory<RotaSolution> createSolverFactory() {
        SolverFactory<RotaSolution> solverFactory = SolverFactory.createFromXmlResource("rotaSolutionSolverConfig.xml");
        TerminationConfig terminationConfig = solverFactory.getSolverConfig().getTerminationConfig();
        terminationConfig.setSecondsSpentLimit((long) timeoutInSeconds);
        return solverFactory;
    }

    // this is mostly to rearrange the shifts/employees so older code can read it...
    // should instead hook in to the rota creation algo and set these fields every time
    private void assignShiftsToEmployees(RotaSolution solve) {
        List<Shift> assignedShifts = solve.getShiftGroups().stream()
                .flatMap(group -> group.getUnderlyingShifts().stream())
                .collect(Collectors.toList());
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
}
