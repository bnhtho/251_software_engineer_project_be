package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.model.Datacore;
import HCMUT.TutorSytem.model.Role;
import HCMUT.TutorSytem.model.Status;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User getInfoFromHcmutSystem(Datacore data) {
        if (data == null) {
            throw new DataNotFoundExceptions("Datacore data not found");
        }

        // Tìm user theo hcmutId
        User user = userRepository.findByHcmutId(data.getHcmutId())
                .orElse(null);

        // Nếu user đã tồn tại: chỉ cập nhật lastLogin, KHÔNG sync từ Datacore nữa
        if (user != null) {
            user.setLastLogin(Instant.now());
            userRepository.save(user);
            return user;
        }

        // Lần đầu đăng nhập: tạo mới user từ Datacore
        user = new User();
        user.setHcmutId(data.getHcmutId());
        user.setLastLogin(Instant.now());

        // Trạng thái mặc định: ACTIVE (id = 1)
        if (user.getStatus() == null) {
            user.setStatus(new Status(1));
        }

        // Role mặc định: STUDENT (id = 3) – chỉ set lần đầu
        user.setRole(new Role(3));

        // Map dữ liệu từ Datacore (chỉ dùng lần đầu)
        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        user.setProfileImage(data.getProfileImage());
        // faculty đã bỏ, dùng quan hệ major
        user.setMajor(data.getMajor());
        user.setAcademicStatus(data.getAcademicStatus().getDisplayName());
        user.setDob(data.getDob());
        user.setPhone(data.getPhone());
        user.setOtherMethodContact(data.getOtherMethodContact());

        return userRepository.save(user);
    }

}
