package com.example.addressbook.model;

import com.example.addressbook.dto.AddressBookDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AddressBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private long phoneNumber;

    public AddressBook(AddressBookDTO addressBookDTO) {
        this.firstName = addressBookDTO.getFirstName();
        this.lastName = addressBookDTO.getLastName();
        this.email = addressBookDTO.getEmail();
        this.address = addressBookDTO.getAddress();
        this.phoneNumber = addressBookDTO.getPhoneNumber();
    }
}
