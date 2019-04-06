package com.parkinsonhardy.autorota.controllers;

import com.parkinsonhardy.autorota.controllers.parsers.EmployeeCreator;
import com.parkinsonhardy.autorota.controllers.parsers.RuleFactory;
import com.parkinsonhardy.autorota.controllers.parsers.ShiftCreator;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.RotaEngineFactoryService;
import com.parkinsonhardy.autorota.engine.planner.PlannerRotaEngine;
import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.exceptions.RuleCreationException;
import com.parkinsonhardy.autorota.model.*;
import com.parkinsonhardy.autorota.util.RotaUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@RestController
public class RotaController {

    private static final Logger logger = LoggerFactory.getLogger(RotaController.class);

    private final RotaRepository rotaRepository;
    private final RotaEngineFactoryService rotaEngineFactoryService;

    @Autowired
    public RotaController(RotaRepository rotaRepository, RotaEngineFactoryService rotaEngineFactoryService) {
        this.rotaRepository = rotaRepository;
        this.rotaEngineFactoryService = rotaEngineFactoryService;
    }

    @PostMapping(path = "/api/rotas/create", consumes = "application/json")
    public Rota createRota(@RequestBody RotaCreationArgs args) throws RotaException, RuleCreationException {
        logger.info(String.format("Received request for Rota creation: %s", args.toString()));

        RotaEngine rotaEngine = rotaEngineFactoryService.createRotaEngine();

        setupShifts(args, rotaEngine);
        setupRules(args, rotaEngine);
        setupEmployees(args, rotaEngine);

        DateTime startDate = DateTime.parse(args.getStartDate());
        DateTime endDate = DateTime.parse(args.getEndDate());

        rotaEngine.setTimeoutInSeconds(args.getTimeout());

        // this db call should be on the pool thread to free up web response workers... Would return DeferredResult<Rota>
        Rota submitted = rotaRepository.save(
                new Rota(args.getName(), LocalDateTime.now(ZoneId.of("Z")), args.getTimeout(), "Submitted", null));

        submitRotaCreationJob(rotaEngine, startDate, endDate, submitted);

        logger.info("Request submitted!");

        return submitted;
    }

    private void setupShifts(RotaCreationArgs args, RotaEngine rotaEngine) {
        ShiftCreator shiftCreator = new ShiftCreator();
        List<ShiftDefinitionArgs> shiftDefinitions = args.getShiftDefinitions();
        for (ShiftDefinitionArgs shiftDefinitionArgs : shiftDefinitions) {
            shiftCreator.addShiftDefinition(rotaEngine, shiftDefinitionArgs);
        }
    }

    private void setupEmployees(RotaCreationArgs args, RotaEngine rotaEngine) throws RotaException {
        EmployeeCreator employeeCreator = new EmployeeCreator();
        for (DoctorArgs doctorArgs : args.getDoctors()) {
            Employee employee = employeeCreator.create(doctorArgs);
            rotaEngine.addEmployee(employee);
        }
    }

    private void setupRules(RotaCreationArgs args, RotaEngine rotaEngine) throws RuleCreationException {
        Set<String> shiftTypes = rotaEngine.getShiftTypes();
        RuleFactory ruleFactory = new RuleFactory(shiftTypes);
        for (RuleArgs ruleArgs : args.getHardRules()) {
            ruleFactory.addRule(rotaEngine, ruleArgs);
        }

        for (RuleArgs ruleArgs : args.getSoftRules()) {
            ruleFactory.addRule(rotaEngine, ruleArgs);
        }
    }

    private void submitRotaCreationJob(RotaEngine rotaEngine, DateTime startDate, DateTime endDate, Rota submitted) {
        ForkJoinPool.commonPool().submit(() -> {
                    try {
                        rotaEngine.assignShifts(startDate, endDate);
                        String rotaPrinted = RotaUtils.stringifyRota(rotaEngine.getEmployees(), startDate, endDate);
                        // should this find by Id again and get from DB? or ok to hang on to reference...?
                        submitted.setStatus("Complete");
                        submitted.setStringRepresentation(rotaPrinted);
                        submitted.setEndDateTime(LocalDateTime.now(ZoneId.of("Z")));
                        rotaRepository.save(submitted);
                    } catch (RotaException e) {
                        String error = "Error, could not create rota";
                        logger.error(error);
                        submitted.setStatus("Error");
                        rotaRepository.save(submitted);
                    }
                }
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleException(Exception e) {
        logger.error("Error caught handling request!", e);
        return e.getMessage();
    }
}
