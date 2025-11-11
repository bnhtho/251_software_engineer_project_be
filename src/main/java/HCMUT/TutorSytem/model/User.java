package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "status", length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "update_date")
    private Instant updateDate;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "hcmut_id", length = 50)
    private String hcmutId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "faculty", length = 100)
    private String faculty;

    @Column(name = "academic_status", length = 100)
    private String academicStatus;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "other_method_contact")
    private String otherMethodContact;

    @OneToMany(mappedBy = "student")
    private Set<FeedbackStudent> feedbackStudents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "tutor")
    private Set<Session> sessions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student")
    private Set<StudentSchedule> studentSchedules = new LinkedHashSet<>();

    @OneToOne(mappedBy = "user")
    private TutorProfile tutorProfile;

    @OneToMany(mappedBy = "tutor")
    private Set<TutorSchedule> tutorSchedules = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student")
    private Set<Session> sessionss = new LinkedHashSet<>();

}