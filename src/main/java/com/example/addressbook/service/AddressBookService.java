package com.example.addressbook.service;

import com.example.addressbook.dto.AddressBookDTO;
import com.example.addressbook.interfaces.IAddressBookService;
import com.example.addressbook.model.AddressBook;
import com.example.addressbook.model.UserAuthentication;
import com.example.addressbook.repository.AddressBookRepository;
import com.example.addressbook.repository.UserAuthenticationRepository;
import com.example.addressbook.util.JwtToken;
import com.example.addressbook.util.SecurityUtil;
import org.hibernate.annotations.Cache;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * AddressBookService class implements IAddressBookService interface
 * and provides the implementation for the methods defined in the interface.
 */
@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    AddressBookRepository addressBookRepository;    // Injecting AddressBookRepository to perform CRUD operations

    @Autowired
    UserAuthenticationRepository userAuthenticationRepository;

    @Autowired
    ModelMapper modelMapper = new ModelMapper();    // ModelMapper to map DTOs to entities and vice versa

    @Autowired
    RedisTemplate<String, Object> redisTemplate; // RedisTemplate to interact with Redis cache

    @Autowired
    JwtToken jwtToken; // JWT token for authentication

    /**
     * This method retrieves all address book entries from the database.
     * It maps each AddressBook entity to AddressBookDTO and returns a list of AddressBookDTO.
     *
     * @return List<AddressBookDTO> - List of AddressBookDTO
     */
    @Override
    @Cacheable(value = "addressBookCache")
    public List<AddressBookDTO> getMyAddressBookData(HttpServletRequest request) {
        String email = SecurityUtil.getAuthenticatedUserEmail();
        UserAuthentication user = userAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        List<AddressBook> addressBooksLists = addressBookRepository.findByUser(user.getUserId()); // Fetch address books by email
        return addressBooksLists.stream()
                .map(addressBook -> modelMapper.map(addressBook, AddressBookDTO.class))
                .toList();
    }

    /**
     * This method retrieves a specific address book entry by its ID.
     * It maps the AddressBook entity to AddressBookDTO and returns it.
     *
     * @param id - The ID of the address book entry
     * @return AddressBookDTO - The address book entry with the specified ID
     */
    @Override
    @Cacheable(value = "addressBookCache", key = "#id")
    public AddressBookDTO getAddressBookDataById(HttpServletRequest request, long id) {
        String sessionToken = request.getHeader("sessionToken");
        // Decode JWT to get expiration
        Date expiryDate = jwtToken.getTokenExpiry(sessionToken);
        long ttl = calculateTTL(expiryDate);

        // Manually set TTL in Redis
        redisTemplate.expire("addressBookCache::" + id, ttl, TimeUnit.SECONDS);


        String email = SecurityUtil.getAuthenticatedUserEmail();
        UserAuthentication user = userAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        AddressBook addressBook = addressBookRepository.findById(id).orElseThrow(() -> new RuntimeException("Address Data not found with id: " + id));
        if(!Objects.equals(addressBook.getUser().getUserId(), user.getUserId())) {
            throw new RuntimeException("Can't Access Address Book Data with id: " + id + ". You are not the owner of that data");
        }
        return modelMapper.map(addressBook, AddressBookDTO.class);
    }

    private long calculateTTL(Date expiryDate) {
        long currentTime = System.currentTimeMillis();
        long expiryTime = expiryDate.getTime();
        return (expiryTime - currentTime) / 1000;  // TTL in seconds
    }

    /**
     * This method creates a new address book entry.
     * It maps the AddressBookDTO to AddressBook entity and saves it to the database.
     *
     * @param addressBookDTO - The address book entry to be created
     * @return AddressBookDTO - The created address book entry
     */
    @Override
    @CacheEvict(value = "addressBookCache", allEntries = true)
    public AddressBookDTO createAddressBookData(AddressBookDTO addressBookDTO) {
        String email = SecurityUtil.getAuthenticatedUserEmail();
        UserAuthentication user = userAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        AddressBook addressBook = modelMapper.map(addressBookDTO, AddressBook.class);
        addressBook.setUser(user);

        addressBook = addressBookRepository.save(addressBook); // Save the address book entry to the database
        return modelMapper.map(addressBook, AddressBookDTO.class);
    }

    /**
     * This method updates an existing address book entry.
     * It retrieves the entry by its ID, updates its fields, and saves it to the database.
     *
     * @param id - The ID of the address book entry to be updated
     * @param updatedAddressBookDTO - The updated address book entry
     * @return boolean - true if the update was successful, false otherwise
     */
    @Override
    @CacheEvict(value = "addressBookCache", key = "#id")
    public boolean updateAddressBookData(long id, AddressBookDTO updatedAddressBookDTO) {
        try {
            String email = SecurityUtil.getAuthenticatedUserEmail();
            UserAuthentication user = userAuthenticationRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            AddressBook addressBook = addressBookRepository.findById(id).orElseThrow(() -> new RuntimeException("Address Book not found with id: " + id));
            if(!Objects.equals(addressBook.getUser().getUserId(), user.getUserId())) {
                throw new RuntimeException("Can't Modified Address Book Data with id: " + id + ". You are not the owner of that data");
            }
            addressBook.setFirstName(updatedAddressBookDTO.getFirstName());
            addressBook.setLastName(updatedAddressBookDTO.getLastName());
            addressBook.setAddress(updatedAddressBookDTO.getAddress());
            System.out.println(updatedAddressBookDTO.getEmail());
            addressBook.setEmail(updatedAddressBookDTO.getEmail());
            addressBook.setPhoneNumber(updatedAddressBookDTO.getPhoneNumber());
            addressBookRepository.save(addressBook);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This method deletes an address book entry by its ID.
     * It throws an exception if the entry is not found.
     *
     * @param id - The ID of the address book entry to be deleted
     */
    @Override
    @CacheEvict(value = "addressBookCache", allEntries = true)
    public void deleteAddressBookData(long id) {
        try {
            addressBookRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Address Book not found with id: " + id);
        }
    }

    /**
     * This method retrieves all address book entries from the database.
     * It maps each AddressBook entity to AddressBookDTO and returns a list of AddressBookDTO.
     *
     * @return List<AddressBookDTO> - List of AddressBookDTO
     */
    @Override
//    @Cacheable(value = "addressBookCache")
    public List<AddressBookDTO> getAllAddressBookData() {
        String email = SecurityUtil.getAuthenticatedUserEmail();
        UserAuthentication user = userAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!user.getRole().equals("ADMIN")) {
            throw new RuntimeException("You are not authorized to access this data");
        }
        List<AddressBook> addressBooksLists = addressBookRepository.findAll(); // Fetch all address books
        return addressBooksLists.stream()
                .map(addressBook -> modelMapper.map(addressBook, AddressBookDTO.class))
                .toList();
    }
}
