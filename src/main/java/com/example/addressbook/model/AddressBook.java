package com.example.addressbook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AddressBook entity class representing the address book table in the database.
 * This class is used to map the address book data to the database.
 * It contains fields for first name, last name, email, address, and phone number.
 * It also includes annotations for JPA entity mapping and validation.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ADDRESS_BOOK")
public class AddressBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String address;

    private long phoneNumber;
}
