package com.example.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ForgotPasswordDTO class to represent the data transfer object for the forgot password functionality.
 * This class contains fields for the user's email and the new password.\
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDTO {

    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
