package com.parkinsonhardy.autorota.helpers;

import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import org.joda.time.DateTime;

public class ShiftCreator {

    private ShiftCreator() {}

    public static Shift createFromDefinition(int shiftId, DateTime startDate, ShiftDefinition shiftDefinition) {
        DateTime endDate;
        if (shiftDefinition.getEndTime().isBefore(shiftDefinition.getStartTime())) {
            endDate = startDate.plusDays(1);
        } else {
            endDate = startDate;
        }
        return new Shift(shiftId, shiftDefinition.getShiftType(),
                startDate.withTime(shiftDefinition.getStartTime()),
                endDate.withTime(shiftDefinition.getEndTime()));
    }
}
