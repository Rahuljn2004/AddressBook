package com.example.addressbook.service;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.model.AddressBook;
import com.example.addressbook.model.UserAuthentication;
import com.example.addressbook.repository.AddressBookRepository;
import com.example.addressbook.repository.UserAuthenticationRepository;
import com.example.addressbook.util.JwtToken;
import com.example.addressbook.util.SecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @Mock
    private UserAuthenticationRepository userAuthenticationRepository;

    @Mock
    private JwtToken jwtToken;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressBookService addressBookService;

    private UserAuthentication user;
    private AddressBook addressBook;
    private AddressBookDTO addressBookDTO;
//    private String validToken = "Bearer validToken";
//    private String invalidToken = "Bearer invalidToken";
    private String validToken;
    private String invalidToken;

    @BeforeAll
    static void init() {
        // This method is called once before all tests in this class
        // You can initialize static resources here if needed
        JwtToken.TOKEN_SECRET = "Lock";
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking User
        user = new UserAuthentication();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setRole("USER");

        // Mocking AddressBook
        addressBook = new AddressBook();
        addressBook.setId(1L);
        addressBook.setFirstName("John");
        addressBook.setLastName("Doe");
        addressBook.setAddress("123 Street");
        addressBook.setEmail("john@example.com");
        addressBook.setPhoneNumber("9876543210");
        addressBook.setUser(user);

        // Mocking AddressBookDTO
        addressBookDTO = new AddressBookDTO();
        addressBookDTO.setFirstName("John");
        addressBookDTO.setLastName("Doe");
        addressBookDTO.setAddress("123 Street");
        addressBookDTO.setEmail("john@example.com");
        addressBookDTO.setPhoneNumber("9876543210");

        jwtToken = Mockito.mock(JwtToken.class);
        validToken = when(jwtToken.createToken(user.getEmail(), user.getRole())).thenReturn(validToken).toString();
        invalidToken = when(jwtToken.createToken(user.getEmail(), user.getRole())).thenReturn(invalidToken).toString();

        System.out.println(validToken);
        System.out.println(invalidToken);

        // Mock SecurityUtil to return email
        SecurityUtil.setAuthenticatedUserEmail("test@example.com");
    }

    @Test
    void testGetMyAddressBookData_Success() {
        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressBookRepository.findByUser(1L)).thenReturn(List.of(addressBook));
        when(modelMapper.map(addressBook, AddressBookDTO.class)).thenReturn(addressBookDTO);

        List<AddressBookDTO> result = addressBookService.getMyAddressBookData(validToken);

        assertEquals(1, result.size());
        assertEquals(addressBookDTO.getFirstName(), result.get(0).getFirstName());
//        redisTemplate.expire("addressBookCacheTest", System.currentTimeMillis() + (60 * 1000), TimeUnit.SECONDS);
        verify(redisTemplate).expire(anyString(), anyLong(), any());
    }

    @Test
    void testGetMyAddressBookData_UserNotFound() {
        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                addressBookService.getMyAddressBookData(validToken));

        assertEquals("User not found with email: test@example.com", exception.getMessage());
    }

    @Test
    void testGetAddressBookDataById_Success() {
        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
        when(modelMapper.map(addressBook, AddressBookDTO.class)).thenReturn(addressBookDTO);

        AddressBookDTO result = addressBookService.getAddressBookDataById(validToken, 1L);

        assertEquals("John", result.getFirstName());
        verify(redisTemplate).expire(anyString(), anyLong(), any());
    }

    @Test
    void testGetAddressBookDataById_NotOwner() {
        UserAuthentication otherUser = new UserAuthentication();
        otherUser.setUserId(2L);

        addressBook.setUser(otherUser);

        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));

        Exception exception = assertThrows(RuntimeException.class, () ->
                addressBookService.getAddressBookDataById(validToken, 1L));

        assertEquals("Can't Access Address Book Data with id: 1. You are not the owner of that data", exception.getMessage());
    }

    @Test
    void testCreateAddressBookData_Success() {
        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(addressBookDTO, AddressBook.class)).thenReturn(addressBook);
        when(addressBookRepository.save(addressBook)).thenReturn(addressBook);
        when(modelMapper.map(addressBook, AddressBookDTO.class)).thenReturn(addressBookDTO);

        AddressBookDTO result = addressBookService.createAddressBookData(addressBookDTO);

        assertEquals("John", result.getFirstName());
    }

    @Test
    void testUpdateAddressBookData_Success() {
        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));

        AddressBookDTO updatedDTO = new AddressBookDTO();
        updatedDTO.setFirstName("Jane");
        updatedDTO.setLastName("Smith");

        boolean result = addressBookService.updateAddressBookData(1L, updatedDTO);

        assertTrue(result);
        assertEquals("Jane", addressBook.getFirstName());
    }

    @Test
    void testUpdateAddressBookData_NotOwner() {
        UserAuthentication otherUser = new UserAuthentication();
        otherUser.setUserId(2L);

        addressBook.setUser(otherUser);

        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));

        AddressBookDTO updatedDTO = new AddressBookDTO();

//        Exception exception = assertThrows(RuntimeException.class, () ->
//                addressBookService.updateAddressBookData(2L, updatedDTO));

//        assertEquals("Can't Modified Address Book Data with id: 1. You are not the owner of that data", exception.getMessage());
    }

    @Test
    void testDeleteAddressBookData_Success() {
        doNothing().when(addressBookRepository).deleteById(1L);

        assertDoesNotThrow(() -> addressBookService.deleteAddressBookData(1L));
    }

    @Test
    void testDeleteAddressBookData_NotFound() {
        doThrow(new RuntimeException("Address Book not found with id: 1"))
                .when(addressBookRepository).deleteById(1L);

        Exception exception = assertThrows(RuntimeException.class, () ->
                addressBookService.deleteAddressBookData(1L));

        assertEquals("Address Book not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetAllAddressBookData_AdminAccess() {
        user.setRole("ADMIN");

        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressBookRepository.findAll()).thenReturn(List.of(addressBook));
        when(modelMapper.map(addressBook, AddressBookDTO.class)).thenReturn(addressBookDTO);

        List<AddressBookDTO> result = addressBookService.getAllAddressBookData();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void testGetAllAddressBookData_NotAdmin() {
        when(userAuthenticationRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () ->
                addressBookService.getAllAddressBookData());

        assertEquals("You are not authorized to access this data", exception.getMessage());
    }
}
