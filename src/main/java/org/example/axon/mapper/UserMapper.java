package org.example.axon.mapper;

import org.example.axon.dto.response.UserResponse;
import org.example.axon.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toDto(User u) {
        return new UserResponse(u.getUserId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getRole());
    }
}
