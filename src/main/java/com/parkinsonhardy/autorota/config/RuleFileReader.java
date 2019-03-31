package com.parkinsonhardy.autorota.config;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
