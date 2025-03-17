package com.example.addressbook.service;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.interfaces.IAddressBookService;
import com.example.addressbook.model.AddressBook;
import com.example.addressbook.repository.AddressBookRepository;
import org.hibernate.annotations.Cache;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AddressBookService class implements IAddressBookService interface
 * and provides the implementation for the methods defined in the interface.
 */
@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    AddressBookRepository addressBookRepository;    // Injecting AddressBookRepository to perform CRUD operations

    ModelMapper modelMapper = new ModelMapper();    // ModelMapper to map DTOs to entities and vice versa

    /**
     * This method retrieves all address book entries from the database.
     * It maps each AddressBook entity to AddressBookDTO and returns a list of AddressBookDTO.
     *
     * @return List<AddressBookDTO> - List of AddressBookDTO
     */
    @Override
    @Cacheable(value = "addressBookCache")
    public List<AddressBookDTO> getAddressBookData() {
        List<AddressBook> addressBooksLists = addressBookRepository.findAll();
        return addressBooksLists.stream()
                .map(addressBook -> modelMapper.map(addressBook, AddressBookDTO.class))
                .toList();
    }

    /**
     * This method retrieves a specific address book entry by its ID.
     * It maps the AddressBook entity to AddressBookDTO and returns it.
     *
     * @param id - The ID of the address book entry
     * @return AddressBookDTO - The address book entry with the specified ID
     */
    @Override
    @Cacheable(value = "addressBookCache", key = "#id")
    public AddressBookDTO getAddressBookDataById(long id) {
        AddressBook addressBook = addressBookRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee Payroll not found with id: " + id));
        return modelMapper.map(addressBook, AddressBookDTO.class);
    }

    /**
     * This method creates a new address book entry.
     * It maps the AddressBookDTO to AddressBook entity and saves it to the database.
     *
     * @param addressBookDTO - The address book entry to be created
     * @return AddressBookDTO - The created address book entry
     */
    @Override
    public AddressBookDTO createAddressBookData(AddressBookDTO addressBookDTO) {
        AddressBook addressBook = addressBookRepository.save(modelMapper.map(addressBookDTO, AddressBook.class));
        return modelMapper.map(addressBook, AddressBookDTO.class);
    }

    /**
     * This method updates an existing address book entry.
     * It retrieves the entry by its ID, updates its fields, and saves it to the database.
     *
     * @param id - The ID of the address book entry to be updated
     * @param updatedAddressBookDTO - The updated address book entry
     * @return boolean - true if the update was successful, false otherwise
     */
    @Override
    public boolean updateAddressBookData(long id, AddressBookDTO updatedAddressBookDTO) {
        try {
            AddressBook addressBook = addressBookRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee Payroll not found with id: " + id));
            addressBook.setFirstName(updatedAddressBookDTO.getFirstName());
            addressBook.setLastName(updatedAddressBookDTO.getLastName());
            addressBook.setAddress(updatedAddressBookDTO.getAddress());
            addressBook.setPhoneNumber(updatedAddressBookDTO.getPhoneNumber());
            addressBookRepository.save(addressBook);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This method deletes an address book entry by its ID.
     * It throws an exception if the entry is not found.
     *
     * @param id - The ID of the address book entry to be deleted
     */
    @Override
    public void deleteAddressBookData(long id) {
        try {
            addressBookRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Employee Payroll not found with id: " + id);
        }
    }
}
