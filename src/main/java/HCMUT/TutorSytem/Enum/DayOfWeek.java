package HCMUT.TutorSytem.Enum;

import lombok.Getter;

/**
 * Enum cho thứ trong tuần
 * 1 = Monday (Thứ 2)
 * 2 = Tuesday (Thứ 3)
 * 3 = Wednesday (Thứ 4)
 * 4 = Thursday (Thứ 5)
 * 5 = Friday (Thứ 6)
 * 6 = Saturday (Thứ 7)
 * 7 = Sunday (Chủ Nhật)
 */
@Getter
public enum DayOfWeek {
    MONDAY(1, "Thứ 2"),
    TUESDAY(2, "Thứ 3"),
    WEDNESDAY(3, "Thứ 4"),
    THURSDAY(4, "Thứ 5"),
    FRIDAY(5, "Thứ 6"),
    SATURDAY(6, "Thứ 7"),
    SUNDAY(7, "Chủ Nhật");

    private final int value;
    private final String displayName;

    DayOfWeek(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Convert từ java.time.DayOfWeek sang custom DayOfWeek enum
     */
    public static DayOfWeek fromJavaDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return values()[javaDayOfWeek.getValue() - 1];
    }

    /**
     * Convert từ int value (1-7) sang DayOfWeek enum
     */
    public static DayOfWeek fromValue(int value) {
        for (DayOfWeek day : values()) {
            if (day.value == value) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day of week value: " + value);
    }
}

