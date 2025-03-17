package com.example.addressbook.controller;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.dto.ResponseDTO;
import com.example.addressbook.model.AddressBook;
import com.example.addressbook.interfaces.IAddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address-book")
public class AddressBookController {

    @Autowired
    IAddressBookService addressBookService;

    @GetMapping("/api/test")
    public String test() {
        return "Test Successful! Welcome to Address Book Application";
    }

    @RequestMapping({"", "/", "/get"})
    public ResponseEntity<ResponseDTO<?>> getAllAddressBook() {
        try {
            List<AddressBookDTO> addressBookData = addressBookService.getAddressBookData();
            return new ResponseEntity<>(new ResponseDTO<List<AddressBookDTO>>("Get All Employees Payroll Data", addressBookData), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO<String>("Get Call Unsuccessful", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO<?>> getContactById(@PathVariable Long id) {
        try {
            AddressBookDTO addressBook = addressBookService.getAddressBookDataById(id);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Get Call for ID Successful", addressBook), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO<String>("Get Call for ID Unsuccessful", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<?>> addInAddressBook(@RequestBody AddressBookDTO AddressBookDTO) {
        try {
            AddressBookDTO newAddressBook = addressBookService.createAddressBookData(AddressBookDTO);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Create New Employee Payroll Data", newAddressBook), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Create New Employee Payroll Data", new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDTO<?>> updateAddressBook(@PathVariable Long id, @RequestBody AddressBookDTO updatedAddressBook) {
        try {
            AddressBookDTO addressBook = addressBookService.getAddressBookDataById(id);
            boolean operation = addressBookService.updateAddressBookData(id, updatedAddressBook);
            if(!operation)
                return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Update Failed", addressBook), HttpStatus.NOT_MODIFIED);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Updated Employee Payroll Data for:" + addressBook + "to below Data ", updatedAddressBook), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Update Failed", new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<?>> deleteAddressBook(@PathVariable Long id) {
        try {
            AddressBookDTO addressBook = addressBookService.getAddressBookDataById(id);
            addressBookService.deleteAddressBookData(id);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Deleted Employee Payroll Data for id: " + id, addressBook), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Employee Payroll Data for id " + id + " is not found", new AddressBookDTO()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Error Deleting Employee Payroll Data for id: " + id, new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }
}
