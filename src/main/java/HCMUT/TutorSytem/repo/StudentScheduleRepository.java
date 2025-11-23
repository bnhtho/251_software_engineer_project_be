package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import HCMUT.TutorSytem.model.StudentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface StudentScheduleRepository extends JpaRepository<StudentSchedule, Integer> {

    /**
     * Tìm các schedule của student theo dayOfWeek
     */
    List<StudentSchedule> findByStudentIdAndDayOfWeek(Integer studentId, DayOfWeek dayOfWeek);

    /**
     * Kiểm tra xem có schedule nào trùng thời gian không
     * @param studentId ID của student
     * @param dayOfWeek Thứ trong tuần
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @return true nếu có trùng lịch
     */
    @Query("SELECT COUNT(s) > 0 FROM StudentSchedule s WHERE s.student.id = :studentId AND s.dayOfWeek = :dayOfWeek " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    boolean existsConflictingSchedule(@Param("studentId") Integer studentId,
                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                      @Param("startTime") Instant startTime,
                                      @Param("endTime") Instant endTime);

    /**
     * Xóa schedule theo sessionId (để xóa khi reject)
     */
    @Query("DELETE FROM StudentSchedule s WHERE s.student.id = :studentId " +
           "AND s.dayOfWeek = :dayOfWeek AND s.startTime = :startTime AND s.endTime = :endTime")
    void deleteByStudentAndSessionInfo(@Param("studentId") Integer studentId,
                                        @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                        @Param("startTime") Instant startTime,
                                        @Param("endTime") Instant endTime);
}

