package com.reliaquest.api.service;

import com.reliaquest.api.constants.CommonConstant;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.httpclient.IHttpClient;
import com.reliaquest.api.model.*;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.request.DeleteEmployeeRequest;
import com.reliaquest.api.response.EmployeeListResponseData;
import com.reliaquest.api.response.EmployeeResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    @Mock
    private IHttpClient httpClient;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployeesSuccess() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee(
                        "1a2b3c4d-1234-5678-9101-abcdef123456",
                        "Alice Johnson",
                        75000,
                        28,
                        "Senior Developer",
                        "alice.johnson@example.com"),
                new Employee(
                        "2b3c4d5e-2345-6789-1011-bcdef2345678",
                        "Bob Williams",
                        82000,
                        35,
                        "Team Lead",
                        "bob.williams@example.com"));

        EmployeeListResponseData responseData = new EmployeeListResponseData();
        responseData.setData(mockEmployees);
        when(httpClient.get(anyString(), eq(EmployeeListResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Alice Johnson", response.getBody().get(0).getName());
    }

    @Test
    void testGetAllEmployeesFailure() {
        EmployeeListResponseData responseData = new EmployeeListResponseData();
        responseData.setData(Collections.emptyList());
        when(httpClient.get(anyString(), eq(EmployeeListResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertEquals(CommonConstant.NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    void testGetEmployeeByIdSuccess() {
        Employee mockEmployee = new Employee("1a2b3c4d-1234-5678-9101-abcdef123456",
                "Alice Johnson",
                75000,
                28,
                "Senior Developer",
                "alice.johnson@example.com");
        EmployeeResponseData responseData = new EmployeeResponseData();
        responseData.setData(mockEmployee);

        when(httpClient.get(contains("/1a2b3c4d-1234-5678-9101-abcdef123456"), eq(EmployeeResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        ResponseEntity<Employee> response = employeeService.getEmployeeById("1a2b3c4d-1234-5678-9101-abcdef123456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alice Johnson", response.getBody().getName());
    }

    @Test
    void testGetEmployeeByIdFailed() {
        when(httpClient.get(contains("/1a2b3c4d-1234-5678-9101"), eq(EmployeeResponseData.class)))
                .thenThrow(new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, CommonConstant.NO_DATA_FOUND));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById("1a2b3c4d-1234-5678-9101-abcdef123456");
        });

        assertEquals(CommonConstant.NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    void testCreateEmployeeSuccess() {
        CreateEmployeeRequest createRequest = new CreateEmployeeRequest("John Doe", 50000, 30, "Engineer");
        Employee mockEmployee = new Employee(
                "25d32a9d-67a1-4552-8d3a-cf291c489887", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeResponseData responseData = new EmployeeResponseData();
        responseData.setData(mockEmployee);

        when(httpClient.post(anyString(), eq(createRequest), eq(EmployeeResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        ResponseEntity<Employee> response = employeeService.createEmployee(createRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void testDeleteEmployeeSuccess() {
        Employee mockEmployee = new Employee(
                "25d32a9d-67a1-4552-8d3a-cf291c489887", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");

        EmployeeResponseData employeeResponseData1 = new EmployeeResponseData();
        employeeResponseData1.setData(mockEmployee);
        when(httpClient.get(contains("/25d32a9d-67a1-4552-8d3a-cf291c489887"), eq(EmployeeResponseData.class)))
                .thenReturn(new ResponseEntity<>(employeeResponseData1, HttpStatus.OK));

        when(httpClient.delete(anyString(), any(DeleteEmployeeRequest.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        ResponseEntity<String> response = employeeService.deleteEmployee("25d32a9d-67a1-4552-8d3a-cf291c489887");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CommonConstant.DELETE_EMPLOYEE_WITH_ID_SUCCESS + "John Doe", response.getBody());
    }

}
