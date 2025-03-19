package com.example.addressbook.service;

import com.example.addressbook.dto.*;
import com.example.addressbook.exception.UserException;
import com.example.addressbook.interfaces.IUserAuthenticationService;
import com.example.addressbook.model.UserAuthentication;
import com.example.addressbook.repository.UserAuthenticationRepository;
import com.example.addressbook.util.JwtToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.example.addressbook.service.MessageProducer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    PasswordEncoder passwordEncoder;    // PasswordEncoder is used to encode passwords.

    @Autowired
    ModelMapper modelMapper;    // ModelMapper is used to map DTOs to entities and vice versa.

    @Autowired
    UserAuthenticationRepository userAuthenticationRepository;      // UserAuthenticationRepository is used to perform CRUD operations on UserAuthentication entity.

    @Autowired
    RedisTemplate<String, Object> redisTemplate;        // RedisTemplate is used to interact with Redis database.

    @Autowired
    MessageProducer messageProducer;        // EventPublisherService is used to publish events.


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
    @Transactional
    public UserAuthenticationDTO register(UserAuthenticationDTO userDTO) throws Exception {
        if (existsByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserException("Email '" + userDTO.getEmail() + "' is already registered!");
        }

        UserAuthentication user = modelMapper.map(userDTO, UserAuthentication.class);
        user.setRole("User");
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        user.setPassword(encodedPassword);

        UserAuthentication savedUser = userAuthenticationRepository.save(user);

        String customMessage = "REGISTER|" + savedUser.getEmail() + "|" + savedUser.getFirstName() + " " + savedUser.getLastName();
        messageProducer.sendMessage(customMessage);

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
    public Optional<UserAuthentication> existsByEmail(String email) {
        return userAuthenticationRepository.findByEmail(email);
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
        Optional<UserAuthentication> user = existsByEmail(loginDTO.getEmail());
        if (user.isPresent() && passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            String sessionToken = tokenUtil.createToken(user.get().getEmail(), user.get().getRole());
//            user.setSessionToken(sessionToken);
//            redisTemplate.opsForValue().set("session:" + sessionToken, user, 10, TimeUnit.MINUTES);
            emailSenderService.sendEmail(user.get().getEmail(),"Logged in Successfully!", "Hii...."+user.get().getFirstName()+"\n\n You have successfully logged in into MyAddressBook App!");
//            userAuthenticationRepository.save(user);
            String customMessage = "LOGIN|" + user.get().getEmail() + "|" + user.get().getFirstName() + " " + user.get().getLastName();
            messageProducer.sendMessage(customMessage);

            return "Congratulations!! You have logged in successfully!\n\n Your JWT token is: " + sessionToken;
        } else if (user.isEmpty()) {
            throw new UserException("Sorry! User not Found!");
        } else if (!passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            throw new UserException("Sorry! Password is incorrect!");
        } else {
            throw new UserException("Sorry! Email or Password is incorrect!");
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
        Optional<UserAuthentication> user = existsByEmail(email);
        if (user.isPresent()) {
            String password = resetPasswordDTO.getNewPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.get().setPassword(encodedPassword);
            user.get().setResetToken(null);
            userAuthenticationRepository.save(user.get());
            emailSenderService.sendEmail(user.get().getEmail(),"Password Reset Successfully!", "Hii...."+user.get().getFirstName()+"\n\n Your password has been reset successfully!");
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
    @Transactional
    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws UserException {
        String email = forgotPasswordDTO.getEmail();
        Optional<UserAuthentication> user = existsByEmail(email);
        if (user.isPresent()) {
            String resetToken = tokenUtil.createToken(email, user.get().getRole());
            user.get().setResetToken(resetToken);
            userAuthenticationRepository.save(user.get());
            String resetLink = "http://localhost:8080/reset-password";
            emailSenderService.sendEmail(user.get().getEmail(), "Password Reset Request",
                    "Hi " + user.get().getFirstName()
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

        String email = tokenUtil.decodeToken(sessionToken);
        Optional<UserAuthentication> user = existsByEmail(email);
        if (user.isPresent()) {
            String password = changePasswordDTO.getNewPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.get().setPassword(encodedPassword);
            user.get().setResetToken(null);
            userAuthenticationRepository.save(user.get());
            emailSenderService.sendEmail(user.get().getEmail(),"Password Changed Successfully!", "Hii...."+user.get().getFirstName()+"\n\n Your password has been changed successfully!");
            return "Password changed successfully!!";
        } else {
            throw new UserException("User not found");
        }
    }
}
