package com.example.addressbook.service;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.model.AddressBook;
import com.example.addressbook.repository.AddressBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Override
    public List<AddressBook> getAddressBookData() {
        return addressBookRepository.findAll();
    }

    @Override
    public AddressBook getAddressBookDataById(long id) {
        return addressBookRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee Payroll not found with id: " + id));
    }

    @Override
    public AddressBook createAddressBookData(AddressBookDTO addressBookDTO) {
        AddressBook addressBook = new AddressBook(addressBookDTO);
        return addressBookRepository.save(addressBook);
    }

    @Override
    public boolean updateAddressBookData(AddressBook addressBook, AddressBookDTO updatedAddressBookDTO) {
        try {
            addressBook.setName(updatedAddressBookDTO.getName());
            addressBook.setAddress(updatedAddressBookDTO.getAddress());
            addressBook.setPhoneNumber(updatedAddressBookDTO.getPhoneNumber());
            addressBookRepository.save(addressBook);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteAddressBookData(long id) {
        addressBookRepository.deleteById(id);
    }
}
