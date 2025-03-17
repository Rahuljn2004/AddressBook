package com.example.addressbook.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserAuthenticationDTO class for user authentication data transfer object.
 * This class is used to validate user input during registration and login.
 * It contains fields for first name, last name, email, and password.
 * It also includes validation annotations to ensure the data meets certain criteria.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationDTO {

    @NotNull(message = "First name is required")
    @Size(min = 3, max = 30, message = "First name must be between 3 and 30 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First Name should always start with a capital letter and contain only letters")
    private String firstName;

    @NotNull(message = "Last name is required. Write 'Unknown' if you don't have it")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s]*$", message = "Last Name should always start with a capital letter and contain only letters")
    @Size(min = 3, max = 30, message = "Last name must be between 3 and 30 characters")
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain an uppercase letter, a lowercase letter, a number, and a special character")
    private String password;

}
