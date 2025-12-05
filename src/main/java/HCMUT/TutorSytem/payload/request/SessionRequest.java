package HCMUT.TutorSytem.payload.request;

import lombok.Data;

import java.time.Instant;

@Data
public class SessionRequest {
    private Integer tutorId;
    private Integer subjectId;
    private Instant startTime;
    private Instant endTime;
    private String format;
    private String location;
    private Integer maxQuantity; // Bắt buộc nhập bởi tutor khi tạo session mới
//    private Integer dayOfWeek; // 0 - Chủ nhật, 1 - Thứ hai, ..., 6 - Thứ bảy
//    private Byte sessionStatusId; // Trạng thái của session
}

