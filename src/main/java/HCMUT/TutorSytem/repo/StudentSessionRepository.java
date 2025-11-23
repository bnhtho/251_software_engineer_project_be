package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.StudentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Tìm các yêu cầu đăng ký đang PENDING cho các session của tutor
     * @param tutorId ID của tutor
     * @param statusId ID của status (PENDING = 1)
     * @return Danh sách StudentSession đang chờ duyệt
     */
    @Query("SELECT ss FROM StudentSession ss WHERE ss.session.tutor.id = :tutorId AND ss.studentSessionStatus.id = :statusId")
    List<StudentSession> findPendingSessionsByTutorId(@Param("tutorId") Integer tutorId, @Param("statusId") Byte statusId);
}

