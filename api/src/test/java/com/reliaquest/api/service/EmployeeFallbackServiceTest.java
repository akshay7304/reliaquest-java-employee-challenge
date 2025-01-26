package com.reliaquest.api.service;

import com.reliaquest.api.httpclient.IHttpClient;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeFallbackServiceTest {

    @Mock
    private IHttpClient httpClient;

    @InjectMocks
    private EmployeeFallbackService employeeFallbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFallbackGetAllEmployees() {
        Throwable throwable = new RuntimeException("Service temporarily unavailable");
        ResponseEntity<List<Employee>> response = employeeFallbackService.fallbackGetAllEmployees(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFallbackGetEmployeesByNameSearch() {
        Throwable throwable = new RuntimeException("service temporarily unavailable");
        ResponseEntity<List<Employee>> response = employeeFallbackService.fallbackGetEmployeesByNameSearch(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFallbackGetEmployeeById() {
        Throwable throwable = new RuntimeException("service temporarily unavailable");
        ResponseEntity<Employee> response = employeeFallbackService.fallbackGetEmployeeById(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFallbackGetHighestSalary() {
        Throwable throwable = new RuntimeException("service temporarily unavailable");
        ResponseEntity<Integer> response = employeeFallbackService.fallbackGetHighestSalary(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody());
    }

    @Test
    void testFallbackGetTopTenHighestEarningEmployeeNames() {
        Throwable throwable = new RuntimeException("service temporarily unavailable");
        ResponseEntity<List<String>> response = employeeFallbackService.fallbackGetTopTenHighestEarningEmployeeNames(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFallbackCreateEmployee() {
        Throwable throwable = new RuntimeException("service temporarily unavailable");
        ResponseEntity<Employee> response = employeeFallbackService.fallbackCreateEmployee(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFallbackDeleteEmployee() {
        Throwable throwable = new RuntimeException("service temporarily unavailable");
        ResponseEntity<String> response = employeeFallbackService.fallbackDeleteEmployee(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to delete employee. Please try again later.", response.getBody());
    }
}
