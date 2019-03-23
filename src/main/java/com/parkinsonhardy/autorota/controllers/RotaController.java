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
import com.parkinsonhardy.autorota.model.DoctorArgs;
import com.parkinsonhardy.autorota.model.RotaCreationArgs;
import com.parkinsonhardy.autorota.model.RuleArgs;
import com.parkinsonhardy.autorota.model.ShiftDefinitionArgs;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class RotaController {

    private ExecutorService rotaThread = Executors.newSingleThreadExecutor();

    private AtomicReference<String> latestRota = new AtomicReference<>();

    private Logger logger = LoggerFactory.getLogger(RotaController.class);

    @PostMapping(path = "/api/rotas/create", consumes = "application/json")
    public String createRota(@RequestBody RotaCreationArgs args) throws RotaException {
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

        rotaThread.submit(() -> {
            try {
                rotaEngine.assignShifts(startDate, endDate);
                latestRota.set(printRota(rotaEngine, startDate, endDate));
            } catch (RotaException e) {
                latestRota.set("Error, could not create rota");
            }
        });

        logger.info("Request submitted!");

        return "Rota creation request submitted...please check the rota view page";
    }

    @GetMapping("/api/rotas/")
    public String getRotas() {
        return latestRota.get();
    }

    private String printRota(RotaEngine rotaEngine, DateTime startDate, DateTime endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(",");
        for (Employee employee : rotaEngine.getEmployees()) {
            sb.append(employee.getName()).append(",");
        }

        sb.append("\n");

        for (DateTime dt = startDate; dt.isBefore(endDate); dt = dt.plusDays(1)) {
            sb.append(dt.toString("yyyy-MM-dd EEE")).append(",");

            for (Employee employee : rotaEngine.getEmployees()) {
                for (Shift shift : employee.getShifts()) {
                    if (shift.getStartTime().withTimeAtStartOfDay().equals(dt)) {
                        sb.append(shift.getShiftType());
                        break;
                    }
                }
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("Number of Days,");
        for (Employee employee : rotaEngine.getEmployees()) {
            int numberOfDays = countShifts(employee, "Day");
            sb.append(numberOfDays).append(",");
        }
        sb.append("\n");

        sb.append("Number of LongDays,");
        for (Employee employee : rotaEngine.getEmployees()) {
            int numberOfDays = countShifts(employee, "LongDay");
            sb.append(numberOfDays).append(",");
        }
        sb.append("\n");

        sb.append("Number of Nights,");
        for (Employee employee : rotaEngine.getEmployees()) {
            int numberOfDays = countShifts(employee, "Night");
            sb.append(numberOfDays).append(",");
        }
        sb.append("\n");

        sb.append("Total Hours,");
        for (Employee employee : rotaEngine.getEmployees()) {
            int totalHours = countHours(employee);
            sb.append(totalHours).append(",");
        }
        sb.append("\n");

        sb.append("Average hours per week,");
        for (Employee employee : rotaEngine.getEmployees()) {
            int totalHours = countHours(employee);
            float averageHours = (float) totalHours / (new Duration(startDate, endDate).getStandardDays() / 7f);
            sb.append(averageHours).append(",");
        }
        sb.append("\n\n");
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
