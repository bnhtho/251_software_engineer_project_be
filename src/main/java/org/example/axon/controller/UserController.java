package org.example.axon.controller;

import lombok.RequiredArgsConstructor;
import org.example.axon.dto.response.UserResponse;
import org.example.axon.model.User;
import org.example.axon.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Authentication auth) {
        User u = userService.findByEmail(auth.getName()); // principal = email
        return new UserResponse(u.getUserId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getRole());
    }
}
