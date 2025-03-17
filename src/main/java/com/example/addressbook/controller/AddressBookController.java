package com.example.addressbook.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address-book")
public class AddressBookController {

    @GetMapping("/api/test")
    public String test() {
        return "Hello from Address Book Controller";
    }

    @GetMapping({"", "/", "/get"})
    public String getContact() {
        return "Get Call Success";
    }

    @GetMapping("/get/{contactId}")
    public String getContactById() {
        return "Get Call Success for id";
    }

    @PostMapping("/create")
    public String addContact() {
        return "Create Call Success";
    }

    @PutMapping("/update/{contactId}")
    public String updateContact() {
        return "Update Call Success";
    }

    @DeleteMapping("/delete/{contactId}")
    public String deleteContact() {
        return "Delete Call Success";
    }
}
