package HCMUT.TutorSytem.scheduler;

import HCMUT.TutorSytem.model.Session;
import HCMUT.TutorSytem.model.SessionStatus;
import HCMUT.TutorSytem.repo.SessionRepository;
import HCMUT.TutorSytem.repo.SessionStatusRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Scheduler tự động cập nhật trạng thái của các session
 * Chạy mỗi giờ để kiểm tra:
 * - Nếu thời gian hiện tại nằm trong khoảng [startTime, endTime] -> IN_PROGRESS
 * - Nếu thời gian hiện tại đã qua endTime -> COMPLETED
 */
@Component
public class SessionStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SessionStatusScheduler.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionStatusRepository sessionStatusRepository;

    /**
     * Task chạy mỗi giờ (cron: 0 phút, mỗi giờ)
     * Format: giây phút giờ ngày tháng ngày_trong_tuần
     * "0 0 * * * *" = chạy vào giây 0, phút 0, mỗi giờ
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateSessionStatus() {
        logger.info("Bắt đầu kiểm tra và cập nhật trạng thái session...");

        Instant now = Instant.now();

        // Chỉ lấy các session có status = SCHEDULED (id = 2)
        List<Session> sessions = sessionRepository.findAll().stream()
                .filter(s -> s.getSessionStatus().getId().equals(SessionStatus.SCHEDULED))
                .toList();

        int inProgressCount = 0;
        int completedCount = 0;

        for (Session session : sessions) {
            Instant startTime = session.getStartTime();
            Instant endTime = session.getEndTime();

            // Kiểm tra nếu session đang diễn ra (now nằm trong [startTime, endTime])
            if (!now.isBefore(startTime) && now.isBefore(endTime)) {
                // Set status = IN_PROGRESS
                SessionStatus inProgressStatus = sessionStatusRepository.findById(SessionStatus.IN_PROGRESS)
                        .orElseThrow(() -> new RuntimeException("SessionStatus IN_PROGRESS not found"));
                session.setSessionStatus(inProgressStatus);
                sessionRepository.save(session);
                inProgressCount++;
                logger.info("Session ID {} đã được cập nhật sang IN_PROGRESS", session.getId());
            }
            // Kiểm tra nếu session đã kết thúc (now >= endTime)
            else if (!now.isBefore(endTime)) {
                // Set status = COMPLETED
                SessionStatus completedStatus = sessionStatusRepository.findById(SessionStatus.COMPLETED)
                        .orElseThrow(() -> new RuntimeException("SessionStatus COMPLETED not found"));
                session.setSessionStatus(completedStatus);
                sessionRepository.save(session);
                completedCount++;
                logger.info("Session ID {} đã được cập nhật sang COMPLETED", session.getId());
            }
        }

        logger.info("Hoàn thành cập nhật trạng thái session. IN_PROGRESS: {}, COMPLETED: {}",
                    inProgressCount, completedCount);
    }
}

