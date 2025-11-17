package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "user_status",
        uniqueConstraints = @UniqueConstraint(name = "uk_status_name", columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;  // tương ứng INT UNSIGNED AUTO_INCREMENT

    @Column(name = "name", length = 50, nullable = false)
    private String name;  // ví dụ: ACTIVE, INACTIVE

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "status")
    private List<User> users;

    public Status(int id){
        switch (id){
            case 1:
                this.id = 1;
                this.name = "ACTIVE";
                this.description = "Trạng thái hoạt động";
                break;
            case 2:
                this.id = 2;
                this.name = "INACTIVE";
                this.description = "Trạng thái ngừng hoạt động";
                break;
            default:
                throw new IllegalArgumentException("Invalid Status ID");
        }
    }
}

