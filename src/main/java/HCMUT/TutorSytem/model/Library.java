package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@jakarta.persistence.Table(name = "`library`")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "catagory", length = 100)
    private String catagory;

    @Column(name = "author")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id",
            foreignKey = @ForeignKey(name = "fk_library_subject"))
    private Subject subject;

    @Column(name = "url")
    private String url;

    @Column(name = "uploaded_date")
    @UpdateTimestamp
    private Instant uploadedDate;

    @Column(name = "uploaded_by")
    private Integer uploadedBy;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportMaterial> reportMaterials = new ArrayList<>();
}