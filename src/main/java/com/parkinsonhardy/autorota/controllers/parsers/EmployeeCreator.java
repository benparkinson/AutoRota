package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.model.DoctorArgs;

public class EmployeeCreator {

    public Employee create(DoctorArgs args) {
        return new Employee(args.getName());
    }
}
