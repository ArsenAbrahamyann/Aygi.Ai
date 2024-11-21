package com.example.demo.utils;

import java.util.Random;

public final class ActivationCode {

    private static final int DIGIT_LENGTH = 6;

    private ActivationCode() {
        throw new AssertionError("No instance for you!");
    }

    public static String generateRandomDigits() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(DIGIT_LENGTH);

        for (int i = 0; i < DIGIT_LENGTH; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }
}