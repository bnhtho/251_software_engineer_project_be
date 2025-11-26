package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.Session;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

    /**
     * Tìm các session khả dụng cho đăng ký:
     * - Status = SCHEDULED (status_id = 2)
     * - Thời gian bắt đầu > hiện tại (chưa diễn ra)
     * - currentQuantity < maxQuantity (còn chỗ)
     */
    @Query("SELECT s FROM Session s WHERE s.sessionStatus.id = 2 " +
           "AND s.startTime > :now " +
           "AND s.currentQuantity < s.maxQuantity")
    List<Session> findAvailableSessions(@Param("now") Instant now);

    /**
     * Tìm các session khả dụng cho đăng ký (with pagination)
     */
    @Query("SELECT s FROM Session s WHERE s.sessionStatus.id = 2 " +
           "AND s.startTime > :now " +
           "AND s.currentQuantity < s.maxQuantity")
    Page<Session> findAvailableSessions(@Param("now") Instant now, Pageable pageable);

    /**
     * Tìm session theo ID với PESSIMISTIC_WRITE lock để tránh race condition
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Session s WHERE s.id = :id")
    Optional<Session> findByIdWithLock(@Param("id") Integer id);

    /**
     * Tìm sessions theo tutor ID
     */
    List<Session> findByTutorId(Integer tutorId);

    /**
     * Tìm các session của tutor trong khoảng thời gian và có status = SCHEDULED
     * @param tutorId ID của tutor
     * @param statusId ID của status (SCHEDULED = 2)
     * @param startOfWeek Thời điểm bắt đầu tuần
     * @param endOfWeek Thời điểm kết thúc tuần
     * @return Danh sách session trong tuần
     */
    @Query("SELECT s FROM Session s WHERE s.tutor.id = :tutorId " +
           "AND s.sessionStatus.id = :statusId " +
           "AND s.startTime >= :startOfWeek " +
           "AND s.startTime < :endOfWeek " +
           "ORDER BY s.startTime ASC")
    List<Session> findTutorScheduledSessionsInWeek(
            @Param("tutorId") Integer tutorId,
            @Param("statusId") Byte statusId,
            @Param("startOfWeek") Instant startOfWeek,
            @Param("endOfWeek") Instant endOfWeek
    );
}
