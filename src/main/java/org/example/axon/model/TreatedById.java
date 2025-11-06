package org.example.axon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class TreatedById implements Serializable {
    private static final long serialVersionUID = -2634635660304914135L;
    @Column(name = "ConditionID", nullable = false)
    private Integer conditionID;

    @Column(name = "DepartmentID", nullable = false)
    private Integer departmentID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TreatedById entity = (TreatedById) o;
        return Objects.equals(this.conditionID, entity.conditionID) &&
                Objects.equals(this.departmentID, entity.departmentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionID, departmentID);
    }

}