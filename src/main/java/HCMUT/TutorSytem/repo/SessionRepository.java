package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.Session;
import jakarta.persistence.LockModeType;
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
     * Tìm session theo ID với PESSIMISTIC_WRITE lock để tránh race condition
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Session s WHERE s.id = :id")
    Optional<Session> findByIdWithLock(@Param("id") Integer id);

    /**
     * Tìm sessions theo tutor ID
     */
    List<Session> findByTutorId(Integer tutorId);
}
