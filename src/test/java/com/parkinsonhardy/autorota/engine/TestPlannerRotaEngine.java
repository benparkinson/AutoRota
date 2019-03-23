package com.parkinsonhardy.autorota.engine;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

// test engine that makes sure we only run for 1 second per solve
public class TestPlannerRotaEngine extends PlannerRotaEngine {

    @Override
    protected SolverFactory<RotaSolution> createSolverFactory() {
        SolverFactory<RotaSolution> solverFactory = super.createSolverFactory();
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setSecondsSpentLimit(1L);
        solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);
        return solverFactory;
    }
}
