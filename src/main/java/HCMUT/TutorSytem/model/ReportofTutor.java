package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "reportof_tutor")
public class ReportofTutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Lob
    @Column(name = "tutor_comment")
    private String tutorComment;

    @Lob
    @Column(name = "student_summary")
    private String studentSummary;

    @Lob
    @Column(name = "student_performance")
    private String studentPerformance;

    @Lob
    @Column(name = "material_used")
    private String materialUsed;

}