package com.reliaquest.api.service;

import com.reliaquest.api.constants.CommonConstant;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.httpclient.IHttpClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.request.DeleteEmployeeRequest;
import com.reliaquest.api.response.EmployeeListResponseData;
import com.reliaquest.api.response.EmployeeResponseData;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    public static final String API_URL = "http://localhost:8112/api/v1/employee";
    private final IHttpClient httpClient;

    @Autowired
    private EmployeeFallbackService employeeFallbackService;

    public EmployeeService(IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private List<Employee> fetchAllEmployees() {
        try {
            ResponseEntity<EmployeeListResponseData> response = httpClient.get(API_URL, EmployeeListResponseData.class);
            if (response.getBody() == null
                    || CollectionUtils.isEmpty(response.getBody().getData())) {
                throw new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, CommonConstant.NO_DATA_FOUND);
            }
            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (Exception e) {
            logger.error("Exception occurred while fetching employees: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while fetching employee list");
        }
        return Collections.emptyList();
    }

    private Employee fetchEmployeeById(String id) {
        try {
            String url = API_URL + "/" + id;
            ResponseEntity<EmployeeResponseData> response = httpClient.get(url, EmployeeResponseData.class);
            if (!Objects.nonNull(response)
                    || response.getBody() == null
                    || response.getBody().getData() == null) {
                throw new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, CommonConstant.NO_DATA_FOUND);
            }
            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (Exception e) {
            logger.error("Exception occurred while fetching employee by ID {}: {}", id, e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Exception occurred while fetching employee by ID " + id);
        }
        return null;
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = fetchAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetEmployeeById")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = fetchEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackCreateEmployee")
    public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest employeeInput) {
        logger.info("Create employee started for employee input: {}", employeeInput);
        try {
            ResponseEntity<EmployeeResponseData> response =
                    httpClient.post(API_URL, employeeInput, EmployeeResponseData.class);
            if (response.getBody() == null || response.getBody().getData() == null) {
                throw new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR, CommonConstant.CREATE_EMPLOYEE_FAILED);
            }
            return ResponseEntity.ok(response.getBody().getData());
        } catch (HttpClientErrorException e) {
            handleClientError(e);
            return null;
        } catch (Exception e) {
            logger.error("Exception occurred while creating employee: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while creating an employee");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetHighestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        List<Employee> employeeList = fetchAllEmployees();
        try {
            Integer highestSalary = employeeList.stream()
                    .map(Employee::getSalary)
                    .max(Integer::compare)
                    .orElse(0);
            logger.info("Highest Salary of employee : {}", highestSalary);
            return ResponseEntity.ok(highestSalary);
        } catch (Exception e) {
            logger.error("Exception occurred while fetching highest salary: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Exception occurred while fetching the highest salary of employees");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetEmployeesByNameSearch")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        if (searchString == null || searchString.trim().isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CommonConstant.STRING_IS_NULL_OR_EMPTY);
        }
        List<Employee> employees = fetchAllEmployees();
        List<Employee> filteredEmployees = null;
        try {
            filteredEmployees = employees.stream()
                    .filter(employee -> employee.getName() != null
                            && employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());

            logger.info("Matching employees count: {}", filteredEmployees.size());
            return ResponseEntity.ok(filteredEmployees);
        } catch (Exception e) {
            logger.error("Exception occurred while searching employees by name {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while searching employees by name");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetTopTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employees = fetchAllEmployees();
        try {
            List<String> topTenEmployeeNames = employees.stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()))
                    .limit(10)
                    .map(Employee::getName)
                    .collect(Collectors.toList());
            logger.info("Top 10 highest salary emp names: {}", topTenEmployeeNames);
            return ResponseEntity.ok(topTenEmployeeNames);
        } catch (Exception e) {
            logger.error("Exception occurred while highest top 10 earning employees name {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while highest top 10 earning employees name");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDeleteEmployee")
    public ResponseEntity<String> deleteEmployee(String id) {
        logger.info("Employee delete started for employee id: {}", id);
        Employee employee = fetchEmployeeById(id);
        try {
            assert employee != null;
            String employeeName = employee.getName();
            DeleteEmployeeRequest deleteRequest = new DeleteEmployeeRequest();
            deleteRequest.setName(employeeName);
            ResponseEntity<Void> deleteResponse = httpClient.delete(API_URL, deleteRequest, Void.class);
            if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                logger.info("Employee deleted successfully for id: {}", id);
                return ResponseEntity.ok(CommonConstant.DELETE_EMPLOYEE_WITH_ID_SUCCESS + employeeName);
            } else {
                logger.error(
                        "Failed to delete employee with ID {}: HTTP status {}", id, deleteResponse.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(CommonConstant.DELETE_EMPLOYEE_WITH_ID_FAILED + id);
            }
        } catch (HttpClientErrorException e) {
            handleClientError(e);
            return null;
        } catch (Exception e) {
            logger.error("Exception occurred while deleting employee with ID {}: {}", id, e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Exception occurred while deleting the employee with ID " + id);
        }
    }

    private void handleClientError(HttpClientErrorException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            throw new EmployeeNotFoundException(CommonConstant.NO_DATA_FOUND);
        } else if (statusCode.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            throw new TooManyRequestsException(
                    CommonConstant.EXCEEDED_THE_NUMBER_OF_REQUESTS);
        }
        logger.error("Client error while fetching employees: {}: {}", e.getStatusCode(), e.getMessage());
        throw e;
    }
}
