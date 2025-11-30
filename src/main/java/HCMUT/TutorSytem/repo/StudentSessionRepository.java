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

    Page<StudentSession> findByStudentId(Integer studentId, Pageable pageable);

    List<StudentSession> findBySessionId(Integer sessionId);

    Optional<StudentSession> findByStudentIdAndSessionId(Integer studentId, Integer sessionId);

    /**
     * Tìm các yêu cầu đăng ký đang PENDING cho các session của tutor
     */
    @Query("""
        SELECT ss FROM StudentSession ss
        WHERE ss.session.tutor.id = :tutorId
          AND ss.registrationStatus.id = :statusId
    """)
    List<StudentSession> findPendingSessionsByTutorId(
            @Param("tutorId") Integer tutorId,
            @Param("statusId") Byte statusId
    );

    /**
     * Tìm các yêu cầu đăng ký đang PENDING cho các session của tutor (có phân trang)
     */
    @Query("""
        SELECT ss FROM StudentSession ss
        WHERE ss.session.tutor.id = :tutorId
          AND ss.registrationStatus.id = :statusId
    """)
    Page<StudentSession> findPendingSessionsByTutorId(
            @Param("tutorId") Integer tutorId,
            @Param("statusId") Byte statusId,
            Pageable pageable
    );

    /**
     * Tìm các StudentSession của student trong tuần với status = CONFIRMED
     */
    @Query("""
        SELECT ss FROM StudentSession ss
        WHERE ss.student.id = :studentId
          AND ss.registrationStatus.id = :statusId
          AND ss.session.startTime >= :startOfWeek
          AND ss.session.startTime < :endOfWeek
        ORDER BY ss.session.startTime ASC
    """)
    List<StudentSession> findStudentConfirmedSessionsInWeek(
            @Param("studentId") Integer studentId,
            @Param("statusId") Byte statusId,
            @Param("startOfWeek") Instant startOfWeek,
            @Param("endOfWeek") Instant endOfWeek
    );
}
