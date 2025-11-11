package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@jakarta.persistence.Table(name = "`library`")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "catagory", length = 100)
    private String catagory;

    @Column(name = "author")
    private String author;

    @Column(name = "subject")
    private String subject;

    @Column(name = "url")
    private String url;

    @Column(name = "uploaded_date")
    private Instant uploadedDate;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

}