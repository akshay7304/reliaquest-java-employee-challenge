package com.reliaquest.api.response;

import com.reliaquest.api.model.Employee;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeListResponseData {
    private List<Employee> data;
    private String message;


}
