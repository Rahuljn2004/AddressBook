package com.example.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * LoginDTO class for user login data transfer object.
 * This class is used to validate user input during login.
 * It contains fields for email and password.
 * It also includes validation annotations to ensure the data meets certain criteria.
 */
@Data
public class LoginDTO {

    @NotNull(message = "Email is required")
    @Email(message = "Enter a valid Email address")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
