package com.example.addressbook.repository;

import com.example.addressbook.model.AddressBook;
import com.example.addressbook.model.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AddressBookRepository is an interface that extends JpaRepository to provide CRUD operations for AddressBook entities.
 * It allows for easy interaction with the database without the need for boilerplate code.
 */
@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {

    /**
     * Custom query to find address books by user ID.
     *
     * @param userId the ID of the user
     * @return a list of AddressBook entities associated with the specified user ID
     */
    @Query(value = "SELECT * FROM ADDRESS_BOOK WHERE user_id = :userId", nativeQuery = true)
    List<AddressBook> findByUser(Long userId);
}
