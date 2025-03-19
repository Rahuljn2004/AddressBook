package com.example.addressbook.controller;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.dto.ResponseDTO;
import com.example.addressbook.exception.AddressBookNotFoundException;
import com.example.addressbook.interfaces.IAddressBookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Address Book operations.
 * Provides endpoints to manage address book data.
 */
@Slf4j
@RestController
@RequestMapping("/api/addressbook")     // Base URL for Address Book API
public class AddressBookController {

    // AddressBookService instance to perform CRUD operations on address book data.
    @Autowired
    IAddressBookService addressBookService;

    /**
     * Endpoint to get all address book entries.
     * @return ResponseEntity with list of AddressBookDTO which contains all address book entries
     */
    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<?>> getMyAllAddressBook(HttpServletRequest request) {
        log.info("Fetching all address book entries");
        try {
            List<AddressBookDTO> addressBookData = addressBookService.getMyAddressBookData(request);
            return new ResponseEntity<>(new ResponseDTO<List<AddressBookDTO>>("Get All Address Book Data", addressBookData), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching address book entries: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseDTO<String>("Get Call Unsuccessful", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to get a specific address book entry by ID.
     * @param id - The ID of the address book entry
     * @return ResponseEntity with AddressBookDTO which contains the address book entry
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<?>> getContactById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Fetching address book entry with ID: {}", id);
        try {
            AddressBookDTO addressBook = addressBookService.getAddressBookDataById(request, id);
            if (addressBook == null)
                throw new AddressBookNotFoundException("Address Book entry not found for ID: " + id);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Get Call for ID Successful", addressBook), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching address book entry with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ResponseDTO<String>("Get Call for ID Unsuccessful", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to add a new address book entry.
     * @param AddressBookDTO - The address book entry to be added
     * @return ResponseEntity with created AddressBookDTO
     */
    @PostMapping("/")
    public ResponseEntity<ResponseDTO<?>> addInAddressBook(@Valid @RequestBody AddressBookDTO AddressBookDTO) {
        log.info("Adding new address book entry: {}", AddressBookDTO);
        try {
            AddressBookDTO newAddressBook = addressBookService.createAddressBookData(AddressBookDTO);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Create New Address Book Data", newAddressBook), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error adding new address book entry: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Create New Address Book Data", new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to update an existing address book entry.
     * @param id - The ID of the address book entry to be updated
     * @param updatedAddressBook - The updated address book entry
     * @return ResponseEntity with updated AddressBookDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<?>> updateAddressBook(@PathVariable Long id, @Valid @RequestBody AddressBookDTO updatedAddressBook, HttpServletRequest request) {
        log.info("Updating address book entry with ID: {} to {}", id, updatedAddressBook);
        try {
            AddressBookDTO addressBook = addressBookService.getAddressBookDataById(request, id);
            boolean operation = addressBookService.updateAddressBookData(id, updatedAddressBook);
            if (addressBook == null)
                throw new AddressBookNotFoundException("Address Book entry not found for ID: " + id);

            if(!operation) {
                log.error("Update operation failed for address book entry with ID: {}", id);
                return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Update Failed", addressBook), HttpStatus.NOT_MODIFIED);
            }
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Updated Address Book Data for:" + addressBook + "to below Data ", updatedAddressBook), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating address book entry with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Update Failed", new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to delete an address book entry by ID.
     * @param id - The ID of the address book entry to be deleted
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<?>> deleteAddressBook(@PathVariable Long id, HttpServletRequest request) {
        log.info("Deleting address book entry with ID: {}", id);
        try {
            AddressBookDTO addressBook = addressBookService.getAddressBookDataById(request, id);
            if (addressBook == null)
                throw new AddressBookNotFoundException("Address Book entry not found for ID: " + id);
            addressBookService.deleteAddressBookData(id);
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Deleted Address Book Data for id: " + id, addressBook), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting address book entry with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ResponseDTO<AddressBookDTO>("Error Deleting Address Book Data for id: " + id, new AddressBookDTO()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to get all address book entries.
     * This endpoint is used only if the user is an admin.
     * @return ResponseEntity with list of AddressBookDTO which contains all address book entries
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<?>> getAllAddressBook() {
        log.info("Fetching all address book entries for admin");
        try {
            List<AddressBookDTO> addressBookData = addressBookService.getAllAddressBookData();
            return new ResponseEntity<>(new ResponseDTO<List<AddressBookDTO>>("Get All Address Book Data", addressBookData), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching all address book entries: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseDTO<String>("Get Call Unsuccessful", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
