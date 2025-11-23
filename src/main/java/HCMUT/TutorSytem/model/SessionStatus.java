package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "session_status",
        uniqueConstraints = @UniqueConstraint(name = "uq_session_status_name", columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    private Byte id;

    @Column(name = "name", length = 32, nullable = false)
    private String name;  // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "description", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "sessionStatus")
    private List<Session> sessions;

    // Constants for easy reference
    public static final byte PENDING = 1;
    public static final byte SCHEDULED = 2;
    public static final byte IN_PROGRESS = 3;
    public static final byte COMPLETED = 4;
    public static final byte CANCELLED = 5;
}

