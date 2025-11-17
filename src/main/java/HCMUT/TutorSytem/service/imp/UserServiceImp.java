package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.model.Datacore;
import HCMUT.TutorSytem.model.Status;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Integer getInfoFromHcmutSystem(Datacore data) {
        if (data == null) throw new DataNotFoundExceptions("Datacore data not found");

        User user = userRepository.findByHcmutId(data.getHcmutId())
                .orElseGet(User::new);

        if (user.getId() == null) {
            user.setHcmutId(data.getHcmutId());
            if (user.getStatus() == null) user.setStatus(new Status(1)); // active
        } else {
            return user.getId();
        }

        // Map các trường từ Datacore -> User
        // role của Datacore là entity Role; User.role là String => lấy name
        if (data.getRole() != null && data.getRole().getName() != null) {
            user.setRole(data.getRole().getName());
        }

        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        user.setProfileImage(data.getProfileImage());
        // Note: faculty removed - both User and Datacore now use major relationship
        user.setMajor(data.getMajor());
        user.setAcademicStatus(data.getAcademicStatus());
        user.setDob(data.getDob());
        user.setPhone(data.getPhone());
        user.setOtherMethodContact(data.getOtherMethodContact());

        // Các trường lifecycle (createdDate/updateDate) do @Creation/@UpdateTimestamp tự lo
        userRepository.save(user);
        return user.getId();
    }
}
