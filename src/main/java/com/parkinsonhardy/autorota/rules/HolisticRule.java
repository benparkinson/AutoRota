package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.engine.ShiftRequirement;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface HolisticRule {

    boolean passesPreCheck(DateTime startDate, DateTime endDate, Map<String, ShiftDefinition> shiftDefinitions, List<ShiftRequirement> shiftRequirements,
                           List<Employee> employees);

    void interrimCheck(List<Employee> employees);

    void finalCheck(List<Employee> employees) throws RotaException;

}
