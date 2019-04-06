package com.parkinsonhardy.autorota.engine.planner;

import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.RotaEngineFactoryService;
import org.springframework.stereotype.Service;

@Service
public class PlannerRotaEngineFactoryService implements RotaEngineFactoryService {

    @Override
    public RotaEngine createRotaEngine() {
        return new PlannerRotaEngine();
    }
}
