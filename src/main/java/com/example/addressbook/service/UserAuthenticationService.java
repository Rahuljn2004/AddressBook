package com.example.addressbook.service;

import com.example.addressbook.dto.*;
import com.example.addressbook.exception.UserException;
import com.example.addressbook.interfaces.IUserAuthenticationService;
import com.example.addressbook.model.UserAuthentication;
import com.example.addressbook.repository.UserAuthenticationRepository;
import com.example.addressbook.util.JwtToken;
import com.example.addressbook.util.ResetToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserAuthenticationService class implements IUserAuthenticationService interface
 * and provides the implementation for the methods defined in the interface.
 * This class is responsible for handling user authentication and registration.
 */
@Service
public class UserAuthenticationService implements IUserAuthenticationService {

    @Autowired
    EmailSenderService emailSenderService;  // EmailSenderService is used to send emails.

    @Autowired
    JwtToken tokenUtil;     // JwtToken is used to generate JWT tokens.

    @Autowired
    ResetToken resetTokenUtil;  // ResetToken is used to generate reset tokens.

    @Autowired
    PasswordEncoder passwordEncoder;    // PasswordEncoder is used to encode passwords.

    @Autowired
    ModelMapper modelMapper;    // ModelMapper is used to map DTOs to entities and vice versa.

    @Autowired
    UserAuthenticationRepository userAuthenticationRepository;      // UserAuthenticationRepository is used to perform CRUD operations on UserAuthentication entity.

    /**
     * This method registers a new user.
     * It takes a UserAuthenticationDTO object as input, maps it to UserAuthentication entity,
     * encodes the password, generates a reset token, and saves the user to the database.
     *
     * @param userDTO - The UserAuthenticationDTO object containing user details.
     * @return UserAuthenticationDTO - The registered user details.
     * @throws Exception - If any error occurs during registration.
     */
    @Override
    public UserAuthenticationDTO register(UserAuthenticationDTO userDTO) throws Exception {
        UserAuthentication user = modelMapper.map(userDTO, UserAuthentication.class);
        user.setRole("User");
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        user.setPassword(encodedPassword);

        userAuthenticationRepository.save(user);
        emailSenderService.sendEmail(user.getEmail(),"Registration Successful!",
                "Hii "+ user.getFirstName() + "..."
                + "\n\n\n\n You have successfully registered into MyAddressBook App!"
                + "Your Profile is given below: \n\n"
                + "First Name: " + user.getFirstName() + "\n"
                + "Last Name: " + user.getLastName() + "\n"
                + "Email: " + user.getEmail() + "\n"
                );

        return modelMapper.map(user, UserAuthenticationDTO.class);
    }

    /**
     * This method checks if a user with the given email exists in the database.
     *
     * @param email - The email address of the user.
     * @return UserAuthentication - The UserAuthentication object if the user exists, null otherwise.
     */
    public UserAuthentication existsByEmail(String email) {
        return userAuthenticationRepository.findByEmail(email);
    }

    /**
     * This method checks if a user with the given ID exists in the database.
     *
     * @param id - The ID of the user.
     * @return UserAuthentication - The UserAuthentication object if the user exists, null otherwise.
     */
    public UserAuthentication existsById(long id) {
        return userAuthenticationRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * This method logs in a user.
     * It takes a LoginDTO object as input, checks if the user exists and if the password is correct,
     * generates a JWT token, and sends a success email to the user.
     *
     * @param loginDTO - The LoginDTO object containing login details.
     * @return String - A success message.
     * @throws UserException - If any error occurs during login.
     */
    @Override
    public String login(LoginDTO loginDTO) throws UserException {
        UserAuthentication user = existsByEmail(loginDTO.getEmail());
        if (user != null && passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            String jwtToken = tokenUtil.createToken(user.getUserId(), user.getRole());
            user.setSessionToken(jwtToken);
            emailSenderService.sendEmail(user.getEmail(),"Logged in Successfully!", "Hii...."+user.getFirstName()+"\n\n You have successfully logged in into MyAddressBook App!");
            userAuthenticationRepository.save(user);
            return "Congratulations!! You have logged in successfully!\n\n Your JWT token is: " + jwtToken;
        } else if (user == null) {
            throw new UserException("Sorry! User not Found!");
        } else if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserException("Sorry! Password is incorrect!");
        } else {
            throw new UserException("Sorry! Email or Password is incorrect!");
        }
    }

    /**
     * This method logs out a user.
     * It takes a JWT token as input and invalidates it.
     *
     * @param token - The JWT token of the user to be logged out.
     * @return String - A success message.
     * @throws UserException - If any error occurs during logout.
     */
    @Override
    public String logout(String token) throws UserException {
        if (tokenUtil.isTokenExpired(token))
            throw new UserException("Session expired");

        long userId = Long.parseLong(tokenUtil.decodeToken(token));
        UserAuthentication user = existsById(userId);
        if (user != null) {
            user.setSessionToken(null);
            userAuthenticationRepository.save(user);
            return "Logout successfully!!";
        } else {
            throw new UserException("User not found");
        }
    }


    /**
     * This method resets the password for a user.
     * It takes a JWT token and a ResetPasswordDTO object as input,
     * verifies if the token is valid, updates the password, and sends a success email to the user.
     *
     * @param resetToken - The JWT token of the user whose password is to be reset.
     * @param resetPasswordDTO - The ResetPasswordDTO object containing new password details.
     * @return String - A success message.
     * @throws UserException - If any error occurs during password reset.
     */
    @Override
    public String resetPassword(String resetToken, ResetPasswordDTO resetPasswordDTO) throws UserException {
        if (tokenUtil.isTokenExpired(resetToken))
            throw new UserException("Token is expired");

        String email = tokenUtil.decodeToken(resetToken);
        UserAuthentication user = existsByEmail(email);
        if (user != null) {
            String password = resetPasswordDTO.getNewPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            user.setResetToken(null);
            userAuthenticationRepository.save(user);
            emailSenderService.sendEmail(user.getEmail(),"Password Reset Successfully!", "Hii...."+user.getFirstName()+"\n\n Your password has been reset successfully!");
            return "Password reset successfully!!";
        } else {
            throw new UserException("User not found" + " " + email + " " + resetToken);
        }
    }


    /**
     * This method sends a reset token to the user's email for password recovery.
     * It takes a ForgotPasswordDTO object as input, verifies if the user exists,
     * generates a reset token, and sends it to the user's email.
     *
     * @param forgotPasswordDTO - The ForgotPasswordDTO object containing user email.
     * @return String - A success message.
     * @throws UserException - If any error occurs during password recovery.
     */
    @Override
    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws UserException {
        String email = forgotPasswordDTO.getEmail();
        UserAuthentication user = existsByEmail(email);
        if (user != null) {
            String resetToken = tokenUtil.createToken(email, user.getRole());
            user.setResetToken(resetToken);
            userAuthenticationRepository.save(user);
            String resetLink = "http://localhost:8080/reset-password";
            emailSenderService.sendEmail(user.getEmail(), "Password Reset Request",
                    "Hi " + user.getFirstName()
                            + "\nIt is came to our attention that you request us for reseting your account password since you forgot it."
                            + "\nIf this request isn't made by you, just don't worry. Just don't share the below credentials with anyone else\n\n"
                            + "Click the link to reset your password: " + resetLink + "\n\nUse the following token in the header: " + resetToken);
            return "Reset token sent to your email!";
        } else {
            throw new UserException("User not found");
        }
    }


    /**
     * This method changes the password for a user.
     * It takes a JWT token and a ChangePasswordDTO object as input,
     * verifies if the token is valid, updates the password, and sends a success email to the user.
     *
     * @param sessionToken - The JWT token of the user whose password is to be changed.
     * @param changePasswordDTO - The ChangePasswordDTO object containing new password details.
     * @return String - A success message.
     * @throws UserException - If any error occurs during password change.
     */
    @Override
    public String changePassword(String sessionToken, ChangePasswordDTO changePasswordDTO) throws UserException {
        if (tokenUtil.isTokenExpired(sessionToken))
            throw new UserException("Session expired!");

        long userId = Long.parseLong(tokenUtil.decodeToken(sessionToken));
        UserAuthentication user = existsById(userId);
        if (user != null) {
            String password = changePasswordDTO.getNewPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            user.setResetToken(null);
            userAuthenticationRepository.save(user);
            emailSenderService.sendEmail(user.getEmail(),"Password Changed Successfully!", "Hii...."+user.getFirstName()+"\n\n Your password has been changed successfully!");
            return "Password changed successfully!!";
        } else {
            throw new UserException("User not found");
        }
    }
}
