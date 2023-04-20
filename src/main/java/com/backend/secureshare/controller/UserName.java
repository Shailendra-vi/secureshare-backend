package com.backend.secureshare.controller;

import com.backend.secureshare.entities.User;
import com.backend.secureshare.repository.UserRepository;


public class UserName {
    
    public static String generateUsername(String email, UserRepository userRepository) {
        // Extract the email prefix (everything before the '@' symbol)
        String[] parts = email.split("@");
        String prefix = parts[0];

        // Generate a random number or string to append to the email prefix
        String randomString = generateRandomString(5); // e.g. "n3z8x"
        String username = prefix + "_" + randomString; // e.g. "johndoe_n3z8x"

        // Check if the generated username already exists in the database
        User user;
        try {
            user = userRepository.findByUsername(username);
            if (user != null) {
                // If the username already exists, generate a new one recursively
                return generateUsername(email, userRepository);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return username;
    }


    private static String generateRandomString(int length) {
        // Generate a random string of the specified length using alphanumeric characters
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (allowedChars.length() * Math.random());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }

}
