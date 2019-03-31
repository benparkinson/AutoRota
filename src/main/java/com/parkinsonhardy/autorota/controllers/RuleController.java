package com.parkinsonhardy.autorota.controllers;

import com.parkinsonhardy.autorota.config.RuleDefinitionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class RuleController {

    private final RuleDefinitionProvider ruleDefinitionProvider;

    @Autowired
    public RuleController(RuleDefinitionProvider ruleDefinitionProvider) {
        this.ruleDefinitionProvider = ruleDefinitionProvider;
    }

    @GetMapping("/api/rules/hard/get")
    public String getHardRules() throws IOException, URISyntaxException {
        return ruleDefinitionProvider.getHardRulesDefinition();
    }

    @GetMapping("/api/rules/soft/get")
    public String getSoftRules() throws IOException, URISyntaxException {
        return ruleDefinitionProvider.getSoftRulesDefinition();
    }
}
