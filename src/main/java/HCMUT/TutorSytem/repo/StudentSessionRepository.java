package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.StudentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSessionRepository extends JpaRepository<StudentSession, Integer> {
    List<StudentSession> findByStudentId(Integer studentId);
    List<StudentSession> findBySessionId(Integer sessionId);
    Optional<StudentSession> findByStudentIdAndSessionId(Integer studentId, Integer sessionId);

    // Use studentSessionStatus.id instead of status string
    List<StudentSession> findByStudentSessionStatusId(Byte statusId);

    // Or use studentSessionStatus.name if needed
    List<StudentSession> findByStudentSessionStatusName(String statusName);
}

