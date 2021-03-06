package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.rules.Rule;
import com.parkinsonhardy.autorota.rules.SoftRule;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class RotaSolution {

    private List<Employee> employees;

    private List<ShiftGroup> shiftGroups;
    private List<Rule> rules;
    private List<SoftRule> softRules;

    private HardSoftScore score;

    // for Planner framework
    public RotaSolution() {}

    public RotaSolution(List<Employee> employees, List<ShiftGroup> shiftGroups, List<Rule> rules, List<SoftRule> softRules) {
        this.employees = employees;
        this.shiftGroups = shiftGroups;
        this.rules = rules;
        this.softRules = softRules;
    }

    @ValueRangeProvider(id = "employees")
    @ProblemFactCollectionProperty
    public List<Employee> getEmployees() {
        return employees;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "shiftGroups")
    public List<ShiftGroup> getShiftGroups() {
        return shiftGroups;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<SoftRule> getSoftRules() {
        return softRules;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
