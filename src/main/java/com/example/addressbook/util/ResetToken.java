package com.example.addressbook.util;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Utility class to generate a unique reset token.
 */
@Component
public class ResetToken {
    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
