package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.HolisticRule;
import com.parkinsonhardy.autorota.rules.Rule;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.*;

public class ExperimentalRotaEngine extends RotaEngine {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentalRotaEngine.class);

    @Override
    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        checkHolisticPreChecks(from, to);

        List<Shift> allShifts = createShifts(from, to);

        Map<Shift, Set<Employee>> employeesUnavailableForShifts = new HashMap<>();
        for (Shift shift : allShifts) {
            employeesUnavailableForShifts.put(shift, new HashSet<>());
        }

        shuffleParameters(from);

        allShifts = sortShifts(allShifts);

        List<ShiftAssignment> shiftAssignments = new ArrayList<>();

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Assigning %d shifts...%n", allShifts.size()));
        }

        int shiftToAssignIndex = 0;

        while (shiftToAssignIndex < allShifts.size()) {
            boolean isLastShiftAssignment = shiftToAssignIndex == allShifts.size() - 1;

            Shift shift = allShifts.get(shiftToAssignIndex);

            List<Employee> availableEmployees = getAvailableEmployees(shift);

            Set<Employee> employeesUnavailableForShift = employeesUnavailableForShifts.get(shift);

            boolean shiftAssigned = tryAssignShift(shiftAssignments, isLastShiftAssignment, shift,
                    availableEmployees, employeesUnavailableForShift);

            if (!shiftAssigned) {
                employeesUnavailableForShifts.get(shift).clear();
                int lastShiftIndex = shiftAssignments.size() - 1;
                if (lastShiftIndex == -1) {
                    throw new RotaException("Unable to assign shifts!");
                }
                ShiftAssignment lastAssignedShiftDetails = shiftAssignments.get(lastShiftIndex);
                shiftAssignments.remove(lastShiftIndex);
                Employee lastAssignedEmployee = lastAssignedShiftDetails.getEmployee();
                Shift lastAssignedShift = lastAssignedShiftDetails.getShift();
                lastAssignedEmployee.removeShift(lastAssignedShift);
                Shift shiftMinusOne = allShifts.get(shiftToAssignIndex - 1);
                if (!shiftMinusOne.equals(lastAssignedShift)) {
                    throw new RotaException("Unexpected previous shift!");
                }
                employeesUnavailableForShifts.get(lastAssignedShift).add(lastAssignedEmployee);
                // go back a step
                shiftToAssignIndex -= 1;
                continue;
            }
            shiftToAssignIndex++;
        }
    }

    private void checkHolisticPreChecks(DateTime from, DateTime to) throws RotaException {
        for (HolisticRule holisticRule : holisticRules) {
            if (!holisticRule.passesPreCheck(from, to, this.shiftDefinitionsByType, this.shiftRequirements,
                    this.employees)) {
                throw new RotaException(String.format("Holistic rule: %s can not pass given the parameters!", holisticRule.toString()));
            }
        }
    }

    private List<Shift> createShifts(DateTime from, DateTime to) throws RotaException {
        List<Shift> allShifts = new ArrayList<>();

        for (DateTime dt = from.withTimeAtStartOfDay();
             dt.isBefore(to.withTimeAtStartOfDay().getMillis());
             dt = dt.plusDays(1)) {
            for (ShiftRequirement shiftRequirement : shiftRequirements) {
                if (!shiftRequirement.shiftRequiredOnDay(DayOfWeek.of(dt.getDayOfWeek()))) {
                    continue;
                }

                ShiftDefinition shiftDefinition = shiftDefinitionsByType.get(shiftRequirement.getShiftType());
                if (shiftDefinition == null) {
                    throw new RotaException(String.format("Could not find shift definition for shift type: %s", shiftRequirement.getShiftType()));
                }
                for (int i = 0; i < shiftRequirement.getMinEmployees(); i++) {
                    Shift shift = createShift(dt, shiftDefinition);

                    allShifts.add(shift);
                }
            }
        }
        return allShifts;
    }

    private void shuffleParameters(DateTime from) {
        long initialSeed = from.getMillis();
        Random r = new Random(initialSeed);
        Collections.shuffle(shiftRequirements, r);
        Collections.shuffle(employees, r);
        Collections.shuffle(rules, r);
        Collections.shuffle(holisticRules, r);
    }

    private List<Shift> sortShifts(List<Shift> allShifts) {
        List<Shift> copy = new ArrayList<>();
        for (Shift shift : allShifts) {
            if (shiftIsOnFridaySaturdaySunday(shift)) {
                copy.add(shift);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Assigning %d weekend shifts first...%n", copy.size()));
        }

        for (Shift shift : allShifts) {
            if (!shiftIsOnFridaySaturdaySunday(shift) &&
                    shiftDefinitionsByType.get(shift.getShiftType()).isAllocateInBlocks()) {
                copy.add(shift);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Assigning %d shifts after night filtering...%n", copy.size()));
        }

        for (Shift shift : allShifts) {
            if (!shiftIsOnFridaySaturdaySunday(shift) &&
                    !shiftDefinitionsByType.get(shift.getShiftType()).isAllocateInBlocks()) {
                copy.add(shift);
            }
        }
        return copy;
    }

    private boolean tryAssignShift(List<ShiftAssignment> shiftAssignments, boolean isLastShiftAssignment,
                                   Shift shift, List<Employee> availableEmployees,
                                   Set<Employee> employeesUnavailableForShift) {
        for (Employee e : availableEmployees) {
            if (!employeesUnavailableForShift.contains(e) && e.isAvailableForShift(shift)) {
                e.addShift(shift);

                if (!scheduleStillValid(isLastShiftAssignment)) {
                    e.removeShift(shift);
                    employeesUnavailableForShift.add(e);
                } else {
                    shiftAssignments.add(new ShiftAssignment(e, shift));
                    return true;
                }
            }
        }
        return false;
    }

    private Shift createShift(DateTime dt, ShiftDefinition shiftDefinition) {
        DateTime endDate;
        if (shiftDefinition.getEndTime().isBefore(shiftDefinition.getStartTime())) {
            endDate = dt.plusDays(1);
        } else {
            endDate = dt;
        }
        return new Shift(shiftDefinition.getShiftType(),
                dt.withTime(shiftDefinition.getStartTime()),
                endDate.withTime(shiftDefinition.getEndTime()));
    }

    private boolean scheduleStillValid(boolean isLastShiftAssignment) {
        for (Rule rule : rules) {
            for (Employee employee : employees) {
                Collections.sort(employee.getShifts());
                if (!rule.shiftsPassesRule(employee.getShifts()))
                    return false;
            }
        }

        if (isLastShiftAssignment) {
            for (HolisticRule holisticRule : holisticRules) {
                if (!holisticRule.passesFinalCheck(employees)) {
                    return false;
                }
            }
        }
        return true;
    }

}
