package HCMUT.TutorSytem.mapper;

import HCMUT.TutorSytem.dto.ScheduleDTO;
import HCMUT.TutorSytem.dto.SubjectDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.model.Schedule;
import HCMUT.TutorSytem.model.Session;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper để convert TutorProfile và User thành TutorDetailDTO
 */
public class TutorDetailMapper {

    /**
     * Map User và TutorProfile thành TutorDetailDTO (bao gồm schedules)
     *
     * @param user User entity
     * @param tutorProfile TutorProfile entity
     * @param schedules Danh sách Schedule của tutor
     * @return TutorDetailDTO với đầy đủ thông tin
     */
    public static TutorDetailDTO toDTO(User user, TutorProfile tutorProfile, List<Schedule> schedules) {
        TutorDetailDTO dto = new TutorDetailDTO();

        // User fields
        dto.setId(user.getId());
        dto.setHcmutId(user.getHcmutId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfileImage(user.getProfileImage());
        dto.setAcademicStatus(user.getAcademicStatus());
        dto.setDob(user.getDob());
        dto.setPhone(user.getPhone());
        dto.setOtherMethodContact(user.getOtherMethodContact());
        dto.setRole(user.getRole().getName());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setUpdateDate(user.getUpdateDate());
        dto.setLastLogin(user.getLastLogin());

        // Major và Department
        if (user.getMajor() != null) {
            dto.setMajorId(user.getMajor().getId());
            dto.setMajorName(user.getMajor().getName());
            if (user.getMajor().getDepartment() != null) {
                dto.setDepartment(user.getMajor().getDepartment().getName());
            }
        }

        // TutorProfile fields
        dto.setTutorProfileId(tutorProfile.getId());
        dto.setBio(tutorProfile.getBio());
        dto.setRating(tutorProfile.getRating());
        dto.setExperienceYears(tutorProfile.getExperienceYears() != null ? tutorProfile.getExperienceYears().intValue() : null);
        dto.setTotalSessionsCompleted(tutorProfile.getTotalSessionsCompleted());

        // Subjects
        if (tutorProfile.getSubjects() != null) {
            List<SubjectDTO> subjectDTOs = tutorProfile.getSubjects().stream()
                    .map(subject -> {
                        SubjectDTO subjectDTO = new SubjectDTO();
                        subjectDTO.setId(subject.getId());
                        subjectDTO.setName(subject.getName());
                        return subjectDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSubjects(subjectDTOs);
        }

        // Schedules - lấy từ bảng schedule chung với user_id = tutorId
        if (schedules != null && !schedules.isEmpty()) {
            List<ScheduleDTO> scheduleDTOs = schedules.stream()
                    .map(TutorDetailMapper::mapScheduleToDTO)
                    .collect(Collectors.toList());
            dto.setSchedules(scheduleDTOs);
        }

        return dto;
    }

    /**
     * Map Schedule entity sang ScheduleDTO
     * Lấy thông tin time, format, location từ session
     *
     * @param schedule Schedule entity
     * @return ScheduleDTO với thông tin đầy đủ từ session
     */
    private static ScheduleDTO mapScheduleToDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setDayOfWeek(schedule.getDayOfWeek());
        scheduleDTO.setCreatedDate(schedule.getCreatedDate());
        scheduleDTO.setUpdateDate(schedule.getUpdateDate());

        // Lấy thông tin từ session
        Session session = schedule.getSession();
        if (session != null) {
            scheduleDTO.setSessionId(session.getId());
            scheduleDTO.setStartTime(session.getStartTime());
            scheduleDTO.setEndTime(session.getEndTime());
            scheduleDTO.setFormat(session.getFormat());
            scheduleDTO.setLocation(session.getLocation());

            // Subject name
            if (session.getSubject() != null) {
                scheduleDTO.setSubjectName(session.getSubject().getName());
            }

            // Tutor name
            if (session.getTutor() != null) {
                User tutorUser = session.getTutor();
                String first = tutorUser.getFirstName() != null ? tutorUser.getFirstName() : "";
                String last = tutorUser.getLastName() != null ? tutorUser.getLastName() : "";
                scheduleDTO.setTutorName((first + " " + last).trim());
            }
        }

        return scheduleDTO;
    }
}

