package com.example.addressbook.interfaces;

import com.example.addressbook.dto.*;
import com.example.addressbook.exception.UserException;

/**
 * Interface for User Authentication Service.
 * It provides methods for user authentication operations.
 * It includes methods for user registration and login.
 */
public interface IUserAuthenticationService {
    UserAuthenticationDTO register(UserAuthenticationDTO userDTO) throws Exception;
    String login(LoginDTO loginDTO) throws UserException;
    String logout(String token) throws UserException;
    String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws UserException;
    String resetPassword(String token, ResetPasswordDTO resetPasswordDTO) throws UserException;
    String changePassword(String token, ChangePasswordDTO changePasswordDTO) throws UserException;

//    String activateUser(String token) throws UserException;
}
