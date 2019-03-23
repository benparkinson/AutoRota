package com.parkinsonhardy.autorota.model;

import java.util.List;

public class RuleArgs {

    private String name;
    private boolean unique;
    private List<RuleParamArgs> params;

    public RuleArgs(String name, boolean unique, List<RuleParamArgs> params) {
        this.name = name;
        this.unique = unique;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public boolean isUnique() {
        return unique;
    }

    public List<RuleParamArgs> getParams() {
        return params;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public void setParams(List<RuleParamArgs> params) {
        this.params = params;
    }
}
