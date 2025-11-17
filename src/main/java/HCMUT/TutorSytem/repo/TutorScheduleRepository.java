package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.TutorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorScheduleRepository extends JpaRepository<TutorSchedule, Long> {
    List<TutorSchedule> findByTutorId(Long tutorId);
}

