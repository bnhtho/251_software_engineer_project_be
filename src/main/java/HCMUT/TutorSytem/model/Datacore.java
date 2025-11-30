package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "datacore")
public class Datacore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "hcmut_id", nullable = false, length = 50)
    private String hcmutId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "academic_status", length = 100)
    private String academicStatus;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "other_method_contact")
    private String otherMethodContact;

    @OneToOne(mappedBy = "datacore")
    private HcmutSso hcmutSso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id",
            foreignKey = @ForeignKey(name = "fk_datacore_major"))
    private Major major;

}