package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
public class TreatedBy {
    @EmbeddedId
    private TreatedById id;

    @MapsId("conditionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ConditionID", nullable = false)
    private Condition conditionID;

    @MapsId("departmentID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "DepartmentID", nullable = false)
    private MedicalDepartment departmentID;

}