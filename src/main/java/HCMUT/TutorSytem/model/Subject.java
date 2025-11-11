package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "faculty", length = 100)
    private String faculty;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "libary_resources")
    private String libaryResources;

    @OneToMany(mappedBy = "subject")
    private Set<Session> sessions = new LinkedHashSet<>();

}