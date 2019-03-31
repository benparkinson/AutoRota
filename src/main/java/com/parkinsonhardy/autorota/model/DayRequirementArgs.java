package com.parkinsonhardy.autorota.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayRequirementArgs {

    private int monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    // don't like this lol
    public Map<Integer, List<DayOfWeek>> getRequirementsGroupedByCount() {
        Map<Integer, List<DayOfWeek>> ret = new HashMap<>();

        put(ret, getMonday(), DayOfWeek.MONDAY);
        put(ret, getTuesday(), DayOfWeek.TUESDAY);
        put(ret, getWednesday(), DayOfWeek.WEDNESDAY);
        put(ret, getThursday(), DayOfWeek.THURSDAY);
        put(ret, getFriday(), DayOfWeek.FRIDAY);
        put(ret, getSaturday(), DayOfWeek.SATURDAY);
        put(ret, getSunday(), DayOfWeek.SUNDAY);

        return ret;
    }

    private void put(Map<Integer, List<DayOfWeek>> map, int count, DayOfWeek dayOfWeek) {
        List<DayOfWeek> dayOfWeeks = map.computeIfAbsent(count, k -> new ArrayList<>());
        dayOfWeeks.add(dayOfWeek);
    }

    @Override
    public String toString() {
        return "DayRequirementArgs{" +
                "monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }
}
