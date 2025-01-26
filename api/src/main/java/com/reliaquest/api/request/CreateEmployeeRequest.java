package com.reliaquest.api.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Positive(message = "Salary must be a positive value") @NotNull(message = "Salary cannot be null") private Integer salary;

    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 75, message = "Age must be no more than 75")
    @NotNull(message = "Age cannot be null") private Integer age;

    @NotBlank(message = "Title is mandatory")
    private String title;
}
