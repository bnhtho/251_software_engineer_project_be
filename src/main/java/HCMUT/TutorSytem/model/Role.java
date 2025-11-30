package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role")
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    public Role (int id) {
        this.id = id;
        switch (id){
            case 1 -> this.name = "ADMIN";
            case 2 -> this.name = "TUTOR";
            case 3 -> this.name = "STUDENT";
            default -> this.name = "UNKNOWN";
        }
    }

}