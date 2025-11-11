package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hcmut_sso")
public class HcmutSso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "hcmut_id", nullable = false, length = 50)
    private String hcmutId;

    @OneToOne
    @JoinColumn(name = "hcmut_id", referencedColumnName = "hcmut_id", insertable = false, updatable = false)
    private Datacore datacore;
}