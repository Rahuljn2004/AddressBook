package com.example.addressbook.repository;

import com.example.addressbook.model.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
}
