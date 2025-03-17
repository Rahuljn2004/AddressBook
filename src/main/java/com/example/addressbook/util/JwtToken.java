package com.example.addressbook.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class to create and decode JWT tokens.
 * It uses the HMAC256 algorithm for signing the tokens.
 */
@Component
public class JwtToken {

    @Autowired
    Environment env;    // Environment is used to access secret credentials and properties from environment variables.

    private static String TOKEN_SECRET;

    /**
     * This method is called after the bean is created.
     * It initializes the TOKEN_SECRET variable with the value from environment variables.
     */
    @PostConstruct
    public void init() {
        TOKEN_SECRET = env.getProperty("CLIENT_SECRET");
    }

    /**
     * This method creates a JWT token with the given user ID and role.
     * It uses HMAC256 algorithm for signing the token.
     *
     * @param id   - The user ID
     * @param role - The user role
     * @return String - The generated JWT token
     */
    public String createToken(Long id, String role)  {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);  // HMAC256 algorithm for signing the token

            String token = JWT.create()// Creating a new JWT token
                    .withSubject(id.toString())             // Setting the subject of the token to user ID
                    .withClaim("role", role)          // Setting the role claim
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                    .sign(algorithm);                       // Signing the token with the algorithm
            return token;

        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This method creates a JWT token with the given email and role.
     * It uses HMAC256 algorithm for signing the token.
     *
     * @param email - The user email
     * @param role  - The user role
     * @return String - The generated JWT token
     */
    public String createToken(String email, String role)  {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);  // HMAC256 algorithm for signing the token

            String token = JWT.create()// Creating a new JWT token
                    .withSubject(email)             // Setting the subject of the token to user ID
                    .withClaim("role", role)          // Setting the role claim
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                    .sign(algorithm);                       // Signing the token with the algorithm
            return token;

        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This method decodes the JWT token and extracts the user ID.
     *
     * @param token   - The JWT token
     * @return String - The extracted user ID
     */
    public String decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();        // Creating a JWT verifier with the same algorithm used for signing
            DecodedJWT decodedJWT = verifier.verify(token);                 // Verifying the token
            return decodedJWT.getSubject();             // Extracting the user ID from the token
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    /**
     * This method checks if the token is expired.
     *
     * @param token - The JWT token
     * @return boolean - true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();        // Creating a JWT verifier with the same algorithm used for signing
            DecodedJWT decodedJWT = verifier.verify(token);                 // Verifying the token
            return decodedJWT.getExpiresAt().before(new Date());          // Checking if the token is expired
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token.");
        } catch (Exception e) {
            throw new RuntimeException("Error while verifying token.");
        }
    }
}