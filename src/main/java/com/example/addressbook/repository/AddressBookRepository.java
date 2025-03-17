package com.example.addressbook.repository;

import com.example.addressbook.model.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AddressBookRepository is an interface that extends JpaRepository to provide CRUD operations for AddressBook entities.
 * It allows for easy interaction with the database without the need for boilerplate code.
 */
@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
}
