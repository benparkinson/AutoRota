package com.parkinsonhardy.autorota.model;

import java.util.List;

public class RotaCreationArgs {

    private List<RuleArgs> softRules;
    private List<DoctorArgs> doctors;
    private List<RuleArgs> hardRules;
    private List<ShiftDefinitionArgs> shiftDefinitions;
    private String startDate, endDate;
    private int timeout;
    private String name;

    public List<DoctorArgs> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<DoctorArgs> doctors) {
        this.doctors = doctors;
    }

    public List<RuleArgs> getSoftRules() {
        return softRules;
    }

    public List<RuleArgs> getHardRules() {
        return hardRules;
    }

    public List<ShiftDefinitionArgs> getShiftDefinitions() {
        return shiftDefinitions;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setSoftRules(List<RuleArgs> softRules) {
        this.softRules = softRules;
    }

    public void setHardRules(List<RuleArgs> hardRules) {
        this.hardRules = hardRules;
    }

    public void setShiftDefinitions(List<ShiftDefinitionArgs> shiftDefinitions) {
        this.shiftDefinitions = shiftDefinitions;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RotaCreationArgs{" +
                "softRules=" + softRules +
                ", doctors=" + doctors +
                ", hardRules=" + hardRules +
                ", shiftDefinitions=" + shiftDefinitions +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
