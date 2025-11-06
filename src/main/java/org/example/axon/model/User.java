package org.example.axon.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column(name = "UserId", nullable = false, length = 36)
    private String userId;

    @Column(name = "FirstName", length = 100)
    private String firstName;

    @Column(name = "LastName", length = 100)
    private String lastName;

    @Column(name = "Email", length = 150)
    private String email;
    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "Password")
    private String password;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Lob
    @Column(name = "Role", nullable = false)
    private String role;

    @OneToOne(mappedBy = "users", fetch = FetchType.LAZY)
    private Doctor doctor;

    @OneToOne(mappedBy = "users", fetch = FetchType.LAZY)
    private Patient patient;


}