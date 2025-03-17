package com.example.addressbook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.addressbook.dto.UserAuthenticationDTO;

/**
 * UserAuthentication class represents the user entity in the database.
 * It contains fields for user details such as first name, last name, email,
 * password, reset token, and role. It is annotated with JPA annotations to
 * map the class to a database table.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNTS")
public class UserAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String resetToken;

    private String role;
}
