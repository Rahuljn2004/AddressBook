package com.example.addressbook.service;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.model.AddressBook;

import java.util.List;

public interface IAddressBookService {
    List<AddressBook> getAddressBookData();
    AddressBook getAddressBookDataById(long id);
    AddressBook createAddressBookData(AddressBookDTO empPayrollDTO);
    boolean updateAddressBookData(AddressBook AddressBook, AddressBookDTO updatedAddressBookDTO);
    void deleteAddressBookData(long id);
}