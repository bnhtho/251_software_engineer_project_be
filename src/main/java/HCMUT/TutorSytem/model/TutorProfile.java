package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "tutor_profile")
public class TutorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subject")
    private String subject;

    @Column(name = "experience_years", columnDefinition = "tinyint UNSIGNED")
    private Short experienceYears;

    @Lob
    @Column(name = "bio")
    private String bio;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "priority", columnDefinition = "int UNSIGNED")
    private Long priority;

    @ColumnDefault("'0'")
    @Column(name = "total_sessions_completed", columnDefinition = "int UNSIGNED not null")
    private Long totalSessionsCompleted;

    @ColumnDefault("1")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;

}