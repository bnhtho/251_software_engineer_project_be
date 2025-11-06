package org.example.axon.controller;

import lombok.RequiredArgsConstructor;
import org.example.axon.configuration.JwtUtils;
import org.example.axon.dto.request.*;
import org.example.axon.dto.response.*;
import org.example.axon.model.User;
import org.example.axon.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwt;
    private final PasswordEncoder encoder;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User u = userService.findByEmail(req.email());
        if (!encoder.matches(req.password(), u.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        String access = jwt.generateAccessToken(u.getEmail());
        String refresh = jwt.generateRefreshToken(u.getEmail());
        return new AuthResponse(access, refresh);
    }

    @PostMapping("/user/register")
    public Object registerUser(@Valid @RequestBody RegisterRequest req) {
        User u = userService.register(req, "USER");
        return Map.of("userId", u.getUserId(), "email", u.getEmail(), "role", u.getRole());
    }

    @PostMapping("/doctor/register")
    public Object registerDoctor(@Valid @RequestBody RegisterRequest req) {
        User u = userService.register(req, "DOCTOR");
        return Map.of("userId", u.getUserId(), "email", u.getEmail(), "role", u.getRole());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody Map<String,String> body) {
        String refreshToken = body.get("refreshToken");
        String email = jwt.getSubject(refreshToken);
        return new AuthResponse(jwt.generateAccessToken(email), jwt.generateRefreshToken(email));
    }
}
