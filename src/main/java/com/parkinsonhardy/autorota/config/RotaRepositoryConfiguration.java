package com.parkinsonhardy.autorota.config;

import com.parkinsonhardy.autorota.model.Rota;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class RotaRepositoryConfiguration implements RepositoryRestConfigurer {

    // allows GUI to see db IDs for Rota class for use in keys in maps
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Rota.class);
    }
}
