package org.example.axon.dto.response;

public record UserResponse(
        String userId, String email, String firstName, String lastName, String phoneNumber, String role
) {}
