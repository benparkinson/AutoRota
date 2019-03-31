package com.parkinsonhardy.autorota.controllers;

import com.parkinsonhardy.autorota.controllers.parsers.EmployeeCreator;
import com.parkinsonhardy.autorota.controllers.parsers.RuleFactory;
import com.parkinsonhardy.autorota.controllers.parsers.ShiftCreator;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.PlannerRotaEngine;
import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import com.parkinsonhardy.autorota.model.*;
import com.parkinsonhardy.autorota.util.RotaUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@Component
public class RotaController {

    private final RotaRepository rotaRepository;

    private Logger logger = LoggerFactory.getLogger(RotaController.class);

    @Autowired
    public RotaController(RotaRepository rotaRepository) {
        this.rotaRepository = rotaRepository;
    }

    @PostMapping(path = "/api/rotas/create", consumes = "application/json")
    public Rota createRota(@RequestBody RotaCreationArgs args) throws RotaException {
        logger.info(String.format("Received request for Rota creation: %s", args.toString()));

        RotaEngine rotaEngine = new PlannerRotaEngine();

        setupShifts(args, rotaEngine);
        setupRules(args, rotaEngine);
        setupEmployees(args, rotaEngine);

        DateTime startDate = DateTime.parse(args.getStartDate());
        DateTime endDate = DateTime.parse(args.getEndDate());

        rotaEngine.setTimeoutInSeconds(args.getTimeout());

        // should this db call be on the pool thread to free up web response workers? Can returned deferred result
        // of the rota in progress if so
        Rota submitted = rotaRepository.save(
                new Rota(args.getName(), LocalDateTime.now(ZoneId.of("Z")), args.getTimeout(), "Submitted", null));

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

        logger.info("Request submitted!");

        return submitted;
    }

    private void setupEmployees(RotaCreationArgs args, RotaEngine rotaEngine) throws RotaException {
        EmployeeCreator employeeCreator = new EmployeeCreator();
        for (DoctorArgs doctorArgs : args.getDoctors()) {
            Employee employee = employeeCreator.create(doctorArgs);
            rotaEngine.addEmployee(employee);
        }
    }

    private void setupRules(RotaCreationArgs args, RotaEngine rotaEngine) {
        Set<String> shiftTypes = rotaEngine.getShiftTypes();
        RuleFactory ruleFactory = new RuleFactory(shiftTypes);
        for (RuleArgs ruleArgs : args.getHardRules()) {
            ruleFactory.addRule(rotaEngine, ruleArgs);
        }

        for (RuleArgs ruleArgs : args.getSoftRules()) {
            ruleFactory.addRule(rotaEngine, ruleArgs);
        }
    }

    private void setupShifts(RotaCreationArgs args, RotaEngine rotaEngine) {
        ShiftCreator shiftCreator = new ShiftCreator();
        List<ShiftDefinitionArgs> shiftDefinitions = args.getShiftDefinitions();
        for (ShiftDefinitionArgs shiftDefinitionArgs : shiftDefinitions) {
            shiftCreator.addShiftDefinition(rotaEngine, shiftDefinitionArgs);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(Exception e) {
        logger.error("Error caught handling request!", e);
    }

}
