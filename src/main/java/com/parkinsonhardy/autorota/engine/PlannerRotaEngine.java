package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.OnlyOneShiftADayRule;
import com.parkinsonhardy.autorota.rules.ShiftBlocksSoftRule;
import org.joda.time.DateTime;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class PlannerRotaEngine extends RotaEngine {

    public PlannerRotaEngine() {
        super();
        addRule(new OnlyOneShiftADayRule());
    }

    @Override
    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        runPreChecks(from, to);

        List<ShiftBlock> shiftBlocks = softRules.stream()
                .filter(rule -> rule.getRuleType() == RuleType.ShiftBlocks)
                .filter(rule -> ((ShiftBlocksSoftRule) rule).isMandatory())
                .map(shiftBlockRule -> ((ShiftBlocksSoftRule) shiftBlockRule).getPreferredShiftBlock())
                .collect(Collectors.toList());

        List<Shift> shifts = createShifts(from, to, shiftBlocks);

        List<ShiftGroup> shiftGroups = createShiftGroups(shifts, shiftBlocks);

        RotaSolution rotaSolution = new RotaSolution(employees, shiftGroups, rules, softRules);

        SolverFactory<RotaSolution> solverFactory = createSolverFactory();

        Solver<RotaSolution> rotaSolutionSolver = solverFactory.buildSolver();

        RotaSolution solve = rotaSolutionSolver.solve(rotaSolution);

        if (!solve.getScore().isFeasible()) {
            throw new RotaException("No feasible solution found within time constraints!");
        }

        HardSoftScore score = solve.getScore();
        System.out.println(String.format("Score: Hard: %s, Soft: %s", score.getHardScore(), score.getSoftScore()));

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

    // this needs a cleanup bigtime
    private List<ShiftGroup> createShiftGroups(List<Shift> shifts, List<ShiftBlock> shiftBlocks) {
        List<Shift> shiftsCopy = new ArrayList<>(shifts);
        shifts.sort(Shift::compareTo);
        shiftsCopy.sort(Shift::compareTo);
        Map<ShiftBlockInstance, List<Shift>> shiftsGroupedByShiftBlock = new HashMap<>();

        for (Shift shift : shifts) {
            ShiftBlockInstance shiftBlockInstance = getMatchingShiftBlock(shift, shiftBlocks);
            if (shiftBlockInstance != null) {
                List<Shift> shiftsInGroup = shiftsGroupedByShiftBlock.computeIfAbsent(shiftBlockInstance, k -> new ArrayList<>());
                shiftsInGroup.add(shift);
                shiftsCopy.remove(shift); // todo stick this in a hashmap since we only remove
            }
        }

        List<ShiftGroup> ret = new ArrayList<>();
        for (Map.Entry<ShiftBlockInstance, List<Shift>> groupedShifts : shiftsGroupedByShiftBlock.entrySet()) {
            groupedShifts.getValue().sort(Shift::compareTo);
            DateTime start = groupedShifts.getValue().get(0).getStartTime();
            int blockCount = 0;
            for (Shift shift : groupedShifts.getValue()) {
                if (shift.getStartTime().equals(start)) {
                    blockCount++;
                }
            }
            List<Shift> copy = new ArrayList<>(groupedShifts.getValue());
            for (int i = 0; i < blockCount; i++) {
                List<Shift> distinctShiftBlock = new ArrayList<>();

                DateTime shiftTime = start;
                for (Shift shift : groupedShifts.getValue()) {
                    if (shift.getStartTime().equals(shiftTime)) {
                        distinctShiftBlock.add(shift);
                        copy.remove(shift);
                        shiftTime = shiftTime.plusDays(1);
                        continue;
                    }
                }

                groupedShifts.getValue().retainAll(copy);
                ret.add(new ShiftGroup(distinctShiftBlock));
            }
        }

        for (Shift shift : shiftsCopy) {
            ret.add(new ShiftGroup(shift));
        }

        ret.sort(ShiftGroup::compareTo);

        return ret;
    }

    private ShiftBlockInstance getMatchingShiftBlock(Shift shift, List<ShiftBlock> shiftBlocks) {
        for (ShiftBlock shiftBlock : shiftBlocks) {
            if (shift.getShiftType().equals(shiftBlock.getShiftType())) {
                DayOfWeek shiftDay = DayOfWeek.of(shift.getStartTime().getDayOfWeek());
                if (shiftBlock.isForDay(shiftDay)) {
                    if (!shiftBlock.isForPreviousDay(shiftDay)) {
                        // first day of shift block
                        return new ShiftBlockInstance(shiftBlock, shift.getStartTime());
                    }
                    // find start time of first shift in block
                    boolean foundFirstDay = false;
                    DateTime start = shift.getStartTime().minusDays(1);
                    while (!foundFirstDay) {
                        if (!shiftBlock.isForPreviousDay(DayOfWeek.of(start.getDayOfWeek()))) {
                            foundFirstDay = true;
                        } else {
                            start = start.minusDays(1);
                        }
                    }
                    return new ShiftBlockInstance(shiftBlock, start);
                }
            }
        }
        return null;
    }

    protected SolverFactory<RotaSolution> createSolverFactory() {
        SolverFactory<RotaSolution> solverFactory = SolverFactory.createFromXmlResource("rotaSolutionSolverConfig.xml");
        TerminationConfig terminationConfig = solverFactory.getSolverConfig().getTerminationConfig();
        terminationConfig.setSecondsSpentLimit((long) timeoutInSeconds);
        return solverFactory;
    }
}
