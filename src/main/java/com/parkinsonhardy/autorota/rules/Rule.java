package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;

public interface Rule {

    boolean employeeCanWorkShift(Employee employee, Shift shift);

}
