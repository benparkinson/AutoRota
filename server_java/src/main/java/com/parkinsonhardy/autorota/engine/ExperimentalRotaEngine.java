package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.HolisticRule;
import com.parkinsonhardy.autorota.rules.Rule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.time.DayOfWeek;
import java.util.*;

public class ExperimentalRotaEngine extends RotaEngine {

    final static Logger logger = LogManager.getLogger(ExperimentalRotaEngine.class);

    @Override
    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        for (HolisticRule holisticRule : holisticRules) {
            if (!holisticRule.passesPreCheck(from, to, this.shiftDefinitionsByType, this.shiftRequirements,
                    this.employees)) {
                throw new RotaException(String.format("Holistic rule: %s can not pass given the parameters!", holisticRule.toString()));
            }
        }

        Map<Shift, Set<Employee>> employeesUnavailableForShifts = new HashMap<>();
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
                    DateTime endDate;
                    if (shiftDefinition.getEndTime().isBefore(shiftDefinition.getStartTime())) {
                        endDate = dt.plusDays(1);
                    } else {
                        endDate = dt;
                    }
                    Shift shift = new Shift(shiftDefinition.getShiftType(),
                            dt.withTime(shiftDefinition.getStartTime()),
                            endDate.withTime(shiftDefinition.getEndTime()));

                    employeesUnavailableForShifts.put(shift, new HashSet<>());
                    allShifts.add(shift);
                }
            }
        }

        long initialSeed = from.getMillis();
        Random r = new Random(initialSeed);
        Collections.shuffle(shiftRequirements, r);
        Collections.shuffle(employees, r);
        Collections.shuffle(rules, r);
        Collections.shuffle(holisticRules, r);

        allShifts = sortShifts(allShifts);

        List<ShiftAssignment> shiftAssignments = new ArrayList<>();

        System.out.printf("Assigning %d shifts...%n", allShifts.size());

        for (int i = 0; i < allShifts.size(); i++) {
            logger.info(i);
            boolean isLastShiftAssignment = i == allShifts.size() - 1;

            Shift shift = allShifts.get(i);

            List<Employee> availableEmployees = getAvailableEmployees(shift);

            if (noEmployeesAvailableForShift(shift, employeesUnavailableForShifts.get(shift), availableEmployees)) {
                employeesUnavailableForShifts.get(shift).clear();

                int lastShiftIndex = shiftAssignments.size() - 1;
                if (lastShiftIndex == -1) {
                    throw new RotaException("Unable to assign shifts!");
                }
                ShiftAssignment lastAssignedShiftDetails = shiftAssignments.get(lastShiftIndex);
                Employee lastAssignedEmployee = lastAssignedShiftDetails.getEmployee();
                Shift lastAssignedShift = lastAssignedShiftDetails.getShift();
                shiftAssignments.remove(lastShiftIndex);
                lastAssignedEmployee.removeShift(lastAssignedShift);
                employeesUnavailableForShifts.get(lastAssignedShift).add(lastAssignedEmployee);
                i -= 2;
                continue;
            }

            Set<Employee> employeesUnavailableForShift = employeesUnavailableForShifts.get(shift);
            boolean shiftAssigned = false;

            for (Employee e : availableEmployees) {
                if (!employeesUnavailableForShift.contains(e) && e.isAvailableForShift(shift)) {
                    e.addShift(shift);

                    if (!scheduleStillValid(isLastShiftAssignment)) {
                        e.removeShift(shift);
                        employeesUnavailableForShift.add(e);
                    } else {
                        shiftAssigned = true;
                        shiftAssignments.add(new ShiftAssignment(e, shift));
                        break;
                    }
                }
            }

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
                Shift shiftMinusOne = allShifts.get(i - 1);
                if (!shiftMinusOne.equals(lastAssignedShift)) {
                    throw new RotaException("Unexpected previous shift!");
                }
                employeesUnavailableForShifts.get(lastAssignedShift).add(lastAssignedEmployee);
                // go back a step
                i -= 2;
                continue;
            }
        }
    }

    private List<Shift> sortShifts(List<Shift> allShifts) {
        List<Shift> copy = new ArrayList<>();
        for (Shift shift : allShifts) {
            if (shiftIsOnFridaySaturdaySunday(shift)) {
                copy.add(shift);
            }
        }

        System.out.printf("Assigning %d weekend shifts first...%n", copy.size());

        for (Shift shift : allShifts) {
            if (!shiftIsOnFridaySaturdaySunday(shift) &&
                    shiftDefinitionsByType.get(shift.getShiftType()).isAllocateInBlocks()) {
                copy.add(shift);
            }
        }

        System.out.printf("Assigning %d shifts after night filtering...%n", copy.size());

        for (Shift shift : allShifts) {
            if (!shiftIsOnFridaySaturdaySunday(shift) &&
                    !shiftDefinitionsByType.get(shift.getShiftType()).isAllocateInBlocks()) {
                copy.add(shift);
            }
        }
        return copy;
    }

    private boolean noEmployeesAvailableForShift(Shift shift, Set<Employee> unavailableEmployees, List<Employee> employees) {
        for (Employee e : employees) {
            if (!unavailableEmployees.contains(e) && e.isAvailableForShift(shift)) {
                return false;
            }
        }
        return true;
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
