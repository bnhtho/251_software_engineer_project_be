package HCMUT.TutorSytem.model;

import HCMUT.TutorSytem.Enum.TutorStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tutor_profile")
public class TutorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "tutor_profile_subject",
            joinColumns = @JoinColumn(name = "tutor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects = new ArrayList<>();

    @Column(name = "experience_years", columnDefinition = "tinyint UNSIGNED")
    private Short experienceYears;

    @Lob
    @Column(name = "bio")
    private String bio;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "priority", columnDefinition = "int UNSIGNED")
    private Integer priority;

    @ColumnDefault("'0'")
    @Column(name = "total_sessions_completed", columnDefinition = "int UNSIGNED not null")
    private Integer totalSessionsCompleted;

    @ColumnDefault("1")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TutorStatus status = TutorStatus.PENDING;

}
