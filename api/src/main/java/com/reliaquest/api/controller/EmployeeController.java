package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {
    @Autowired
    private EmployeeService employeeService;

    public EmployeeController() {}

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return employeeService.getEmployeeById(id);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return employeeService.getEmployeesByNameSearch(searchString);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return employeeService.getHighestSalaryOfEmployees();
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return employeeService.getTopTenHighestEarningEmployeeNames();
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid CreateEmployeeRequest employeeInput) {
        return employeeService.createEmployee(employeeInput);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return employeeService.deleteEmployee(id);
    }
}
