package com.example.addressbook.repository;

import com.example.addressbook.model.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * UserAuthenticationRepository is an interface that extends JpaRepository to provide CRUD operations for UserAuthentication entities.
 * It allows for easy interaction with the database without the need for boilerplate code.
 */
@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {
    @Query(value = "SELECT * FROM ACCOUNTS WHERE EMAIL = :email", nativeQuery = true)
    UserAuthentication findByEmail(@Param("email") String email);
    UserAuthentication findByResetToken(String resetToken);
}
