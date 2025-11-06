package org.example.axon.services;

import java.util.UUID;

import org.example.axon.dto.request.RegisterRequest;
import org.example.axon.model.Doctor;
import org.example.axon.model.User;
import org.example.axon.repository.DoctorRepository;
import org.example.axon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final DoctorRepository doctorRepo;

    @Transactional
    public User register(RegisterRequest req, String role) {
        if (userRepo.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already used");

        User u = new User();
        u.setUserId(UUID.randomUUID().toString());
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setPhoneNumber(req.phoneNumber());
        u.setRole(role);

        // Lưu user trước
        User savedUser = userRepo.save(u);

        // Nếu là DOCTOR thì tạo thêm bản ghi bên Doctors
        if ("DOCTOR".equalsIgnoreCase(role)) {
            Doctor doctor = new Doctor();

            // với @MapsId, chỉ cần set user
            doctor.setUsers(savedUser);

            // nếu muốn, set các field default khác tại đây
            // doctor.setSpecialization("General"); ...

            // lưu doctor
            doctorRepo.save(doctor);

            // cập nhật lại reference trong User cho đồng bộ object (không bắt buộc, nhưng nên)
            savedUser.setDoctor(doctor);
        }

        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
