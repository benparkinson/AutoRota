package com.parkinsonhardy.autorota.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class RuleFileReader implements RuleDefinitionProvider {

    @Override
    public String getHardRulesDefinition() throws URISyntaxException, IOException {
        return readFileAsString("hardRules.json");
    }

    @Override
    public String getSoftRulesDefinition() throws URISyntaxException, IOException {
        return readFileAsString("softRules.json");
    }

    private String readFileAsString(String s) throws IOException, URISyntaxException {
        URL resource = RuleFileReader.class.getClassLoader().getResource(s);
        List<String> fileLines = Files.readAllLines(Paths.get(resource.toURI()));
        return String.join("\n", fileLines);
    }
}
