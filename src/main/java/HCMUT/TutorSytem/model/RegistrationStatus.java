package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "registration_status",
        uniqueConstraints = @UniqueConstraint(name = "uq_registration_status_name", columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    private Byte id;

    @Column(name = "name", length = 32, nullable = false)
    private String name;  // PENDING, CONFIRMED, CANCELLED, REJECTED

    @Column(name = "description", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "registrationStatus")
    private List<StudentSession> studentSessions;

    @OneToMany(mappedBy = "registrationStatus")
    private List<TutorProfile> tutorProfiles;

    // Constants for easy reference
    public static final byte PENDING = 1;
    public static final byte CONFIRMED = 2;
    public static final byte CANCELLED = 3;
    public static final byte REJECTED = 4;
}

