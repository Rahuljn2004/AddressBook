package com.example.addressbook.service;

import com.example.addressbook.dto.LoginDTO;
import com.example.addressbook.dto.UserAuthenticationDTO;
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
        String token = resetTokenUtil.generateResetToken();

        user.setPassword(encodedPassword);
        user.setResetToken(token);

        userAuthenticationRepository.save(user);
        emailSenderService.sendEmail(user.getEmail(),"Registration Successful!",
                "Hii "+ user.getFirstName() + "..."
                + "\n\n\n\n You have successfully registered into MyAddressBook App!"
                + "Your Profile is given below: \n\n"
                + "First Name: " + user.getFirstName() + "\n"
                + "Last Name: " + user.getLastName() + "\n"
                + "Email: " + user.getEmail() + "\n"
                + "Token: " + token + "\n"
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


            emailSenderService.sendEmail(user.getEmail(),"Logged in Successfully!", "Hii...."+user.getFirstName()+"\n\n You have successfully logged in into MyAddressBook App!");
            return "Congratulations!! You have logged in successfully!";
        } else if (user == null) {
            throw new UserException("Sorry! User not Found!");
        } else if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserException("Sorry! Password is incorrect!");
        } else {
            throw new UserException("Sorry! Email or Password is incorrect!");
        }
    }
}
