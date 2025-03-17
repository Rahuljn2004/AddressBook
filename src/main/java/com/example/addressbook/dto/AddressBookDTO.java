package com.example.addressbook.dto;

import com.example.addressbook.model.AddressBook;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressBookDTO {

    @NotNull(message = "First name is required")
    @Size(min = 3, max = 30, message = "First name must be between 3 and 30 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First Name should always start with a capital letter and contain only letters")
    private String firstName;

    @NotNull(message = "Last name is required. Write 'Unknown' if you don't have it")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last Name should always start with a capital letter and contain only letters")
    @Size(min = 3, max = 30, message = "Last name must be between 3 and 30 characters")
    private String lastName;

    @NotNull(message = "Address is required.")
    @Pattern(regexp = "^[0-9A-Z][0-9a-zA-Z\\s-/]*$", message = "Address should start with a number or capital letter")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 30 characters")
    private String address;

    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be 10 digits long")
    private long phoneNumber;
}
