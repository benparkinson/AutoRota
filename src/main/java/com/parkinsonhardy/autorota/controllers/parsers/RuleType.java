package com.parkinsonhardy.autorota.controllers.parsers;

public enum RuleType {

    MinHoursBetweenShifts,
    MaxAverageHoursPerWeek(true, false),
    MaxConsecutiveShifts,
    MaxHoursPerWeek,
    NoMoreThanOneWeekendInARow,
    MinHoursBreakAfterConsecutiveShifts,
    AverageHoursBalance(false, true),
    ShiftBlocks(false, true),
    ShiftTypeBalance(false, true);

    private final boolean holistic;
    private final boolean soft;

    RuleType() {
        this(false, false);
    }

    RuleType(boolean holistic, boolean soft) {

        this.holistic = holistic;
        this.soft = soft;
    }

    public boolean isHolistic() {
        return holistic;
    }

    public boolean isSoft() {
        return soft;
    }
}
