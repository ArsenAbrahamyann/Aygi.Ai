package com.example.demo.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActivationCodeTest {

    @Test
    public void testGenerateRandomDigits() {
        String activationCode = ActivationCode.generateRandomDigits();

        System.out.println("Generated activation code: " + activationCode);

        assertEquals(6, activationCode.length());

        assertTrue(activationCode.matches("\\d+"));
    }
}