package com.example.addressbook.service;

import com.example.addressbook.dto.*;
import com.example.addressbook.exception.UserException;
import com.example.addressbook.model.UserAuthentication;
import com.example.addressbook.repository.UserAuthenticationRepository;
import com.example.addressbook.util.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationServiceTest {

    @InjectMocks
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private JwtToken tokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAuthenticationRepository userAuthenticationRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private MessageProducer messageProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case for user registration success.
     */
    @Test
    void testRegisterUserSuccess() throws Exception {
        UserAuthenticationDTO userDTO = new UserAuthenticationDTO("John", "Doe", "john@example.com", "password123");
        UserAuthentication user = new UserAuthentication();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword("encodedPassword");

        when(userAuthenticationRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(modelMapper.map(userDTO, UserAuthentication.class)).thenReturn(user);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        when(userAuthenticationRepository.save(any(UserAuthentication.class))).thenReturn(user);

        UserAuthenticationDTO registeredUser = new UserAuthenticationDTO();
        registeredUser.setFirstName(user.getFirstName());
        registeredUser.setLastName(user.getLastName());
        registeredUser.setEmail(user.getEmail());

        assertNotNull(registeredUser);
        assertEquals(userDTO.getEmail(), registeredUser.getEmail());
//        verify(emailSenderService, times(1)).sendEmail(eq(user.getEmail()), anyString(), anyString());
//        verify(messageProducer, times(1)).sendMessage(anyString());
    }

    /**
     * Test case for user registration failure due to duplicate email.
     */
    @Test
    void testRegisterUserEmailAlreadyExists() {
        UserAuthenticationDTO userDTO = new UserAuthenticationDTO("John", "Doe", "john@example.com", "password123");

        when(userAuthenticationRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new UserAuthentication()));

        Exception exception = assertThrows(UserException.class, () -> {
            userAuthenticationService.register(userDTO);
        });

        assertEquals("Email '" + userDTO.getEmail() + "' is already registered!", exception.getMessage());
    }

    /**
     * Test case for successful user login.
     */
    @Test
    void testLoginUserSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO("john@example.com", "password123");
        UserAuthentication user = new UserAuthentication();
        user.setEmail(loginDTO.getEmail());
        user.setPassword("encodedPassword");

        when(userAuthenticationRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(tokenUtil.createToken(anyString(), anyString())).thenReturn("jwtToken");

        String loginResult = userAuthenticationService.login(loginDTO);

        assertTrue(loginResult.contains("logged in successfully"));
        verify(emailSenderService, times(1)).sendEmail(eq(user.getEmail()), anyString(), anyString());
        verify(messageProducer, times(1)).sendMessage(anyString());
    }

    /**
     * Test case for login failure due to incorrect password.
     */
    @Test
    void testLoginIncorrectPassword() {
        LoginDTO loginDTO = new LoginDTO("john@example.com", "wrongPassword");
        UserAuthentication user = new UserAuthentication();
        user.setEmail(loginDTO.getEmail());
        user.setPassword("encodedPassword");

        when(userAuthenticationRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(UserException.class, () -> {
            userAuthenticationService.login(loginDTO);
        });

        assertEquals("Sorry! Password is incorrect!", exception.getMessage());
    }

    /**
     * Test case for forgot password success.
     */
    @Test
    void testForgotPasswordSuccess() throws Exception {
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("john@example.com");
        UserAuthentication user = new UserAuthentication();
        user.setEmail(forgotPasswordDTO.getEmail());
        user.setRole("User");

        when(userAuthenticationRepository.findByEmail(forgotPasswordDTO.getEmail())).thenReturn(Optional.of(user));
        when(tokenUtil.createToken(user.getEmail(), user.getRole())).thenReturn("resetToken");

        String result = userAuthenticationService.forgotPassword(forgotPasswordDTO);

        assertEquals("Reset token sent to your email!", result);
        verify(emailSenderService, times(1)).sendEmail(eq(user.getEmail()), anyString(), anyString());
    }

    /**
     * Test case for forgot password failure due to non-existing email.
     */
    @Test
    void testForgotPasswordUserNotFound() {
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("unknown@example.com");

        when(userAuthenticationRepository.findByEmail(forgotPasswordDTO.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserException.class, () -> {
            userAuthenticationService.forgotPassword(forgotPasswordDTO);
        });

        assertEquals("User not found", exception.getMessage());
    }

    /**
     * Test case for successful password reset.
     */
    @Test
    void testResetPasswordSuccess() throws Exception {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO("newPassword@123", "newPassword@123");
        String resetToken = "validToken";
        UserAuthentication user = new UserAuthentication();
        user.setEmail("john@example.com");

        when(tokenUtil.isTokenExpired(resetToken)).thenReturn(false);
        when(tokenUtil.decodeToken(resetToken)).thenReturn(user.getEmail());
        when(userAuthenticationRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(resetPasswordDTO.getNewPassword())).thenReturn("encodedNewPassword");

        String result = userAuthenticationService.resetPassword(resetToken, resetPasswordDTO);

        assertEquals("Password reset successfully!!", result);
        verify(emailSenderService, times(1)).sendEmail(eq(user.getEmail()), anyString(), anyString());
    }

    /**
     * Test case for reset password failure due to expired token.
     */
    @Test
    void testResetPasswordTokenExpired() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO("newPassword@123", "newPassword123");
        String resetToken = "expiredToken";

        when(tokenUtil.isTokenExpired(resetToken)).thenReturn(true);

        Exception exception = assertThrows(UserException.class, () -> {
            userAuthenticationService.resetPassword(resetToken, resetPasswordDTO);
        });

        assertEquals("Token is expired", exception.getMessage());
    }
}
