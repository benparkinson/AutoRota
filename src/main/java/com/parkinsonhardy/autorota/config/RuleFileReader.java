package com.parkinsonhardy.autorota.config;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class RuleFileReader implements RuleDefinitionProvider {

    @Override
    public String getHardRulesDefinition() throws IOException {
        return readFileAsString("/hardRules.json");
    }

    @Override
    public String getSoftRulesDefinition() throws IOException {
        return readFileAsString("/softRules.json");
    }

    private String readFileAsString(String s) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(s);
        byte[] bytes = IOUtils.toByteArray(resourceAsStream);
        return new String(bytes);
    }
}
