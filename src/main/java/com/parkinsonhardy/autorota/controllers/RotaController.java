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
        ShiftCreator shiftCreator = new ShiftCreator();
        List<ShiftDefinitionArgs> shiftDefinitions = args.getShiftDefinitions();
        for (ShiftDefinitionArgs shiftDefinitionArgs : shiftDefinitions) {
            shiftCreator.addShiftDefinition(rotaEngine, shiftDefinitionArgs);
        }
        Set<String> shiftTypes = rotaEngine.getShiftTypes();

        RuleFactory ruleFactory = new RuleFactory(shiftTypes);
        for (RuleArgs ruleArgs : args.getHardRules()) {
            ruleFactory.addRule(rotaEngine, ruleArgs);
        }

        for (RuleArgs ruleArgs : args.getSoftRules()) {
            ruleFactory.addRule(rotaEngine, ruleArgs);
        }

        EmployeeCreator employeeCreator = new EmployeeCreator();
        for (DoctorArgs doctorArgs : args.getDoctors()) {
            Employee employee = employeeCreator.create(doctorArgs);
            rotaEngine.addEmployee(employee);
        }

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
                        String rotaPrinted = printRota(rotaEngine, startDate, endDate);
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

    private String printRota(RotaEngine rotaEngine, DateTime startDate, DateTime endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(",");
        String prefix = "";
        for (Employee employee : rotaEngine.getEmployees()) {
            sb.append(prefix);
            prefix = ",";
            sb.append(employee.getName());
        }

        sb.append("\n");

        for (DateTime dt = startDate; dt.isBefore(endDate); dt = dt.plusDays(1)) {
            sb.append(dt.toString("yyyy-MM-dd EEE")).append(",");

            prefix = "";
            for (Employee employee : rotaEngine.getEmployees()) {
                sb.append(prefix);
                prefix = ",";
                for (Shift shift : employee.getShifts()) {
                    if (shift.getStartTime().withTimeAtStartOfDay().equals(dt)) {
                        sb.append(shift.getShiftType());
                        break;
                    }
                }
            }
            sb.append("\n");
        }

        sb.append("Number of Days,");
        prefix = "";
        for (Employee employee : rotaEngine.getEmployees()) {
            int numberOfDays = countShifts(employee, "Day");
            sb.append(prefix);
            prefix = ",";
            sb.append(numberOfDays);
        }
        sb.append("\n");

        sb.append("Number of LongDays,");
        prefix = "";
        for (Employee employee : rotaEngine.getEmployees()) {
            int numberOfDays = countShifts(employee, "LongDay");
            sb.append(prefix);
            prefix = ",";
            sb.append(numberOfDays);
        }
        sb.append("\n");

        sb.append("Number of Nights,");
        prefix = "";
        for (Employee employee : rotaEngine.getEmployees()) {
            int numberOfDays = countShifts(employee, "Night");
            sb.append(prefix);
            prefix = ",";
            sb.append(numberOfDays);
        }
        sb.append("\n");

        sb.append("Total Hours,");
        prefix = "";
        for (Employee employee : rotaEngine.getEmployees()) {
            int totalHours = countHours(employee);
            sb.append(prefix);
            prefix = ",";
            sb.append(totalHours);
        }
        sb.append("\n");

        sb.append("Average hours per week,");
        prefix = "";
        for (Employee employee : rotaEngine.getEmployees()) {
            int totalHours = countHours(employee);
            float averageHours = (float) totalHours / (new Duration(startDate, endDate).getStandardDays() / 7f);
            sb.append(prefix);
            prefix = ",";
            sb.append(averageHours);
        }
        return sb.toString();
    }

    private int countHours(Employee employee) {
        int totalHours = 0;
        for (Shift shift : employee.getShifts()) {
            totalHours += ShiftHelper.calculateShiftHours(shift.getStartTime(), shift.getEndTime());
        }
        return totalHours;
    }

    private int countShifts(Employee employee, String shiftType) {
        int totalShifts = 0;
        for (Shift shift : employee.getShifts()) {
            if (shift.getShiftType().equals(shiftType)) {
                totalShifts += 1;
            }
        }
        return totalShifts;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(Exception e) {
        logger.warn("Bad Gateway exception", e);
    }

}
