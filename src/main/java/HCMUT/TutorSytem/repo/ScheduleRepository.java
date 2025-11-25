package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import HCMUT.TutorSytem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    /**
     * Tìm các schedule của user theo dayOfWeek
     */
    List<Schedule> findByUserIdAndDayOfWeek(Integer userId, DayOfWeek dayOfWeek);

    /**
     * Kiểm tra xem có schedule nào trùng thời gian không
     * Dựa vào session's start_time và end_time để kiểm tra conflict
     * @param userId ID của user (student hoặc tutor)
     * @param dayOfWeek Thứ trong tuần
     * @param startTime Thời gian bắt đầu của session mới (Instant)
     * @param endTime Thời gian kết thúc của session mới (Instant)
     * @return true nếu có trùng lịch
     */
    @Query("SELECT COUNT(s) > 0 FROM Schedule s " +
           "WHERE s.user.id = :userId AND s.dayOfWeek = :dayOfWeek " +
           "AND ((s.session.startTime < :endTime AND s.session.endTime > :startTime))")
    boolean existsConflictingSchedule(@Param("userId") Integer userId,
                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                      @Param("startTime") Instant startTime,
                                      @Param("endTime") Instant endTime);

    /**
     * Xóa schedule theo userId và sessionId (để xóa khi reject/cancel)
     */
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.user.id = :userId AND s.session.id = :sessionId")
    void deleteByUserIdAndSessionId(@Param("userId") Integer userId,
                                     @Param("sessionId") Integer sessionId);

    /**
     * Tìm tất cả schedule của user theo userId
     */
    List<Schedule> findByUserId(Integer userId);
}

