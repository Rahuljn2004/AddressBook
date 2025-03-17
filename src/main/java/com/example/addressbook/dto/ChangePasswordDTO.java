package com.example.addressbook.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotNull(message = "Current password is required")
    private String currentPassword;

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
