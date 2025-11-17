package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {
    List<Major> findByDepartmentId(Integer departmentId);
}

