package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;



@Entity
@Table(name = "report_material")
@Data
public class ReportMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_material_report"))
    private ReportofTutor report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id",
            foreignKey = @ForeignKey(name = "fk_report_material_library"))
    private Library library;

    @Column(length = 255)
    private String title;

    @Column(length = 1024)
    private String url;

}
