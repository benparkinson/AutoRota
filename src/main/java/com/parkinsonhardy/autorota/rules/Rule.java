package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;

import java.util.List;

public interface Rule {

    boolean shiftsPassesRule(List<Shift> shifts);

    String getName();

}
