package com.example.addressbook.controller;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.dto.ResponseDTO;
import com.example.addressbook.model.AddressBook;
import com.example.addressbook.service.IAddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address-book")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @GetMapping("/api/test")
    public String test() {
        return "Test Successful! Welcome to Address Book Application";
    }

    @RequestMapping({"", "/", "/get"})
    public ResponseEntity<ResponseDTO> getAllAddressBook() {
        List<AddressBook> addressBookData = addressBookService.getAddressBookData();
        return new ResponseEntity<>(new ResponseDTO("Get All Employees Payroll Data", addressBookData), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO> getContactById(@PathVariable Long id) {
        try {
            AddressBook addressBook = addressBookService.getAddressBookDataById(id);
            return new ResponseEntity<>(new ResponseDTO("Get Call for ID Successful", addressBook), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("Get Call for ID Unsuccessful", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> addInAddressBook(@RequestBody AddressBookDTO AddressBookDTO) {
        AddressBook newAddressBook = addressBookService.createAddressBookData(AddressBookDTO);
        return new ResponseEntity<>(new ResponseDTO("Create New Employee Payroll Data", newAddressBook), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDTO> updateAddressBook(@PathVariable Long id, @RequestBody AddressBookDTO updatedAddressBook) {
        AddressBook addressBook = addressBookService.getAddressBookDataById(id);
        boolean operation = addressBookService.updateAddressBookData(addressBook, updatedAddressBook);
        if(!operation)
            return new ResponseEntity<>(new ResponseDTO("Update Failed", addressBook), HttpStatus.NOT_MODIFIED);
        return new ResponseEntity<>(new ResponseDTO("Updated Employee Payroll Data for:" + addressBook + "to below Data ", updatedAddressBook), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteAddressBook(@PathVariable Long id) {
        try {
            AddressBook addressBook = addressBookService.getAddressBookDataById(id);
            addressBookService.deleteAddressBookData(id);
            return new ResponseEntity<ResponseDTO>(new ResponseDTO("Deleted Employee Payroll Data for id: " + id, addressBook), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<ResponseDTO>(new ResponseDTO("Employee Payroll Data for id " + id + " is not found", new AddressBookDTO()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<ResponseDTO>(new ResponseDTO("Error Deleting Employee Payroll Data for id: " + id, new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }
}
