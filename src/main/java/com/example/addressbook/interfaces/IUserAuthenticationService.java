package com.example.addressbook.interfaces;

import com.example.addressbook.dto.UserAuthenticationDTO;
import com.example.addressbook.dto.LoginDTO;
import com.example.addressbook.exception.UserException;

/**
 * Interface for User Authentication Service.
 * It provides methods for user authentication operations.
 * It includes methods for user registration and login.
 */
public interface IUserAuthenticationService {
    UserAuthenticationDTO register(UserAuthenticationDTO userDTO) throws Exception;
    String login(LoginDTO loginDTO) throws UserException;
//    String activateUser(String token) throws UserException;
}
