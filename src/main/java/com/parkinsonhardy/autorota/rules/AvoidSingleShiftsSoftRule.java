package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AvoidSingleShiftsSoftRule extends SoftRule {

    public AvoidSingleShiftsSoftRule(int weight) {
        super(weight);
    }

    @Override
    protected int innerCalculateScore(List<Employee> employees) {

        float totalShiftCount = 0;
        float singleShiftCount = 0;

        for (Employee employee : employees) {
            Set<DateTime> datesWithShifts = new HashSet<>();
            for (Shift shift : employee.getShifts()) {
                datesWithShifts.add(shift.getStartTime().withTimeAtStartOfDay());
            }
            totalShiftCount += datesWithShifts.size();
            for (DateTime dateWithShift : datesWithShifts) {
                DateTime previousDay = dateWithShift.minusDays(1);
                DateTime nextDay = dateWithShift.plusDays(1);

                if (!datesWithShifts.contains(previousDay) && !datesWithShifts.contains(nextDay)) {
                    singleShiftCount++;
                }
            }
        }

        if (totalShiftCount == 0) {
            return PERFECT_SCORE;
        }

        return Math.round(PERFECT_SCORE - ((singleShiftCount / totalShiftCount) * PERFECT_SCORE));
    }


    private boolean moreThanOneDayBetweenShifts(Shift lastShiftChecked, Shift shift) {
        Duration duration = new Duration(lastShiftChecked.getStartTime(), shift.getStartTime());
        return duration.getStandardDays() > 1;
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.AvoidSingleShifts;
    }
}
