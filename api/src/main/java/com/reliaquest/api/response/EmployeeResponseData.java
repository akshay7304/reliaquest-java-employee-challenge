package com.reliaquest.api.response;

import com.reliaquest.api.model.Employee;
import lombok.Data;

@Data
public class EmployeeResponseData {
    private Employee data;
    private String message;
    private String status;
}
