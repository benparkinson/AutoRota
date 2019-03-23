package com.parkinsonhardy.autorota.model;

public class RuleParamArgs {

    private String name;
    private String type;
    private String input;

    public RuleParamArgs(String name, String type, String input) {
        this.name = name;
        this.type = type;
        this.input = input;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getInput() {
        return input;
    }

    @Override
    public String toString() {
        return "RuleParamArgs{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", input='" + input + '\'' +
                '}';
    }
}
