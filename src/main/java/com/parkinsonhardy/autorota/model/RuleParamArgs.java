package com.parkinsonhardy.autorota.model;

// conflating the two types of rule param args is pretty grim. Ideally there would be a better solution...
public class RuleParamArgs {

    private String name;
    private String type;
    private String input;
    private String from;
    private String to;

    public RuleParamArgs(String name, String type, String input, String from, String to) {
        this.name = name;
        this.type = type;
        this.input = input;
        this.from = from;
        this.to = to;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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
