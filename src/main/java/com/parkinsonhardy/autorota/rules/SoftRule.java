package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.Employee;

import java.util.List;

public abstract class SoftRule {

    protected static final int PERFECT_SCORE = 100;

    private final int weight;

    public SoftRule(int weight) {
        this.weight = weight;
    }

    protected int weighResult(int result) {
        return weight * result;
    }

    public int calculateSoftScore(List<Employee> employees) {
        if (weight == 0)
            return 0;

        return weighResult(innerCalculateScore(employees));
    }

    protected abstract int innerCalculateScore(List<Employee> employees);

    public abstract RuleType getRuleType();
}
