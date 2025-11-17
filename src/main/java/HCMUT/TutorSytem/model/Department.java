package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "department",
        uniqueConstraints = @UniqueConstraint(name = "uk_department_name", columnNames = "name"))
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)          // INT UNSIGNED AUTO_INCREMENT
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // Quan hệ 1-n với major
    @OneToMany(mappedBy = "department", orphanRemoval = false)
    private List<Major> majors = new ArrayList<>();
}
