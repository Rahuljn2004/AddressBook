package com.example.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResetPasswordDTO class is used to transfer data related to password reset requests.
 * It contains fields for the user's email, old password, new password, and confirmation of the new password.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {

    @NotNull(message = "New Password is required")
    @Size(min = 8, message = "New Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#_,.()^~+/<>:;`-])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "New Password must contain an uppercase letter, a lowercase letter, a number, and a special character")
    private String newPassword;

    @NotNull(message = "Confirm Password is required to make sure you entered the correct password")
    @Size(min = 8, message = "New Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#_,.()^~+/<>:;`-])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "New Password must contain an uppercase letter, a lowercase letter, a number, and a special character")
    private String confirmPassword;
}
