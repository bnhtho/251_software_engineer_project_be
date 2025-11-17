package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "major",
        uniqueConstraints = @UniqueConstraint(name = "uk_major_combo",
                columnNames = {"major_code", "program_code"}))
@Data
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_major_department"))
    private Department department;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "major_code", nullable = false, length = 7)
    private String majorCode;

    @Column(name = "program_code", nullable = false, length = 3)
    private String programCode;

    @Column(name = "note", length = 255)
    private String note;

    @OneToMany(mappedBy = "major")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "major")
    private List<Datacore> datacores = new ArrayList<>();

}
