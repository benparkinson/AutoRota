package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OnlyOneShiftADayRule implements Rule {

    @Override
    public boolean shiftsPassesRule(List<Shift> shifts) {
        Set<DateTime> daysWithShifts = new HashSet<>();

        for (Shift shift : shifts) {
            if (!daysWithShifts.add(shift.getStartTime().withTimeAtStartOfDay())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getName() {
        return "One Shift a day rule";
    }
}
