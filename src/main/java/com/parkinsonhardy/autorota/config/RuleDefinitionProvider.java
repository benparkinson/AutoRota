package com.parkinsonhardy.autorota.config;

import java.io.IOException;
import java.net.URISyntaxException;

public interface RuleDefinitionProvider {

    String getHardRulesDefinition() throws URISyntaxException, IOException;

    String getSoftRulesDefinition() throws URISyntaxException, IOException;

}
