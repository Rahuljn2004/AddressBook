package com.example.addressbook.controller;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.dto.ResponseDTO;
import com.example.addressbook.model.AddressBook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address-book")
public class AddressBookController {

    @GetMapping("/api/test")
    public String test() {
        return "Hello from Address Book Controller";
    }

    @GetMapping({"", "/", "/get"})
    public ResponseEntity<ResponseDTO> getContact() {
        AddressBookDTO addressBook = new AddressBookDTO();
        return new ResponseEntity<>(new ResponseDTO("Get Contacts", addressBook), HttpStatus.OK);
    }

    @GetMapping("/get/{contactId}")
    public ResponseEntity<ResponseDTO> getContactById() {
        AddressBookDTO addressBook = new AddressBookDTO();
        return new ResponseEntity<>(new ResponseDTO("Get Contact", addressBook), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> addContact(@RequestBody AddressBookDTO addressBookDTO) {
        return new ResponseEntity<>(new ResponseDTO("Add New Contact", addressBookDTO), HttpStatus.OK);
    }

    @PutMapping("/update/{contactId}")
    public ResponseEntity<ResponseDTO> updateContact(@PathVariable Long contactId, @RequestBody AddressBookDTO addressBookDTO) {
        return new ResponseEntity<>(new ResponseDTO("Update the Contact", addressBookDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{contactId}")
    public ResponseEntity<ResponseDTO> deleteContact(@PathVariable Long contactId) {
        return new ResponseEntity<ResponseDTO>(new ResponseDTO("Deleted the contact with id: " + contactId, new AddressBookDTO()), HttpStatus.OK);
    }
}
