package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeFallbackService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeFallbackService.class);

    public ResponseEntity<List<Employee>> fallbackGetAllEmployees(Throwable throwable) {
        logger.error("Fallback triggered for getAllEmployees. Error: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of(new Employee()));
    }

    public ResponseEntity<List<Employee>> fallbackGetEmployeesByNameSearch(Throwable throwable) {
        logger.error("Fallback triggered for employee search by name. Error: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of());
    }

    public ResponseEntity<Employee> fallbackGetEmployeeById(Throwable throwable) {
        logger.error("Fallback triggered for getEmployeeById. Error: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    public ResponseEntity<Integer> fallbackGetHighestSalary(Throwable throwable) {
        logger.error("Fallback triggered for getHighestSalary. Error: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(0);
    }

    public ResponseEntity<List<String>> fallbackGetTopTenHighestEarningEmployeeNames(Throwable throwable) {
        logger.error(
                "Fallback triggered for getTopTenHighestEarningEmployeeNames. Error: {}",
                throwable.getMessage(),
                throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of());
    }

    public ResponseEntity<Employee> fallbackCreateEmployee(Throwable throwable) {
        logger.error("Fallback triggered for employee creation. Error: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    public ResponseEntity<String> fallbackDeleteEmployee(Throwable throwable) {
        logger.error("Fallback triggered for deleteEmployee. Error: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Failed to delete employee. Please try again later.");
    }
}
