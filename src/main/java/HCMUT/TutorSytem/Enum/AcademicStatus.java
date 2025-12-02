package HCMUT.TutorSytem.Enum;

import lombok.Data;

public enum AcademicStatus {
    FRESHMAN("Năm nhất"),
    SOPHOMORE("Năm hai"),
    JUNIOR("Năm ba"),
    SENIOR("Năm tư trở đi"),
    EXCHANGE("Sinh viên trao đổi"),
    GRADUATED("Đã tốt nghiệp"),
    TEACHER("Giảng viên");

    private final String displayName;

    AcademicStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
