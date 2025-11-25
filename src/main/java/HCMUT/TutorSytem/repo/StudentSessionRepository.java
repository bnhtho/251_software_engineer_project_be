package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.StudentSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSessionRepository extends JpaRepository<StudentSession, Integer> {
    List<StudentSession> findByStudentId(Integer studentId);
    Page<StudentSession> findByStudentId(Integer studentId, Pageable pageable); // Thêm overload với Pageable
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

    /**
     * Tìm các yêu cầu đăng ký đang PENDING cho các session của tutor (with pagination)
     * @param tutorId ID của tutor
     * @param statusId ID của status (PENDING = 1)
     * @param pageable Pagination parameters
     * @return Page of StudentSession đang chờ duyệt
     */
    @Query("SELECT ss FROM StudentSession ss WHERE ss.session.tutor.id = :tutorId AND ss.studentSessionStatus.id = :statusId")
    Page<StudentSession> findPendingSessionsByTutorId(@Param("tutorId") Integer tutorId, @Param("statusId") Byte statusId, Pageable pageable);

    /**
     * Tìm các StudentSession của student trong khoảng thời gian và có status = CONFIRMED
     * @param studentId ID của student
     * @param statusId ID của status (CONFIRMED = 2)
     * @param startOfWeek Thời điểm bắt đầu tuần
     * @param endOfWeek Thời điểm kết thúc tuần
     * @return Danh sách StudentSession đã được confirm trong tuần
     */
    @Query("SELECT ss FROM StudentSession ss WHERE ss.student.id = :studentId " +
           "AND ss.studentSessionStatus.id = :statusId " +
           "AND ss.session.startTime >= :startOfWeek " +
           "AND ss.session.startTime < :endOfWeek " +
           "ORDER BY ss.session.startTime ASC")
    List<StudentSession> findStudentConfirmedSessionsInWeek(
            @Param("studentId") Integer studentId,
            @Param("statusId") Byte statusId,
            @Param("startOfWeek") Instant startOfWeek,
            @Param("endOfWeek") Instant endOfWeek
    );
}

