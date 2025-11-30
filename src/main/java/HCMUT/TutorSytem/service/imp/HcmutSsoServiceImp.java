package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.exception.MethodNotAllowExceptions;
import HCMUT.TutorSytem.model.Datacore;
import HCMUT.TutorSytem.model.HcmutSso;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.payload.request.LoginRequest;
import HCMUT.TutorSytem.repo.HcmutSsoRepository;
import HCMUT.TutorSytem.service.HcmutSsoService;
import HCMUT.TutorSytem.service.UserService;
import HCMUT.TutorSytem.util.JWTHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HcmutSsoServiceImp implements HcmutSsoService {
    @Autowired
    private HcmutSsoRepository hcmutSsoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTHelper jwtHelper;

    @Override
    public String validateUser(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        HcmutSso user = hcmutSsoRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with email: " + email));
        if(passwordEncoder.matches(password, user.getPassword())){
            Datacore data = user.getDatacore();
//            System.out.println("Datacore data: " + data);
            User userInfo = userService.getInfoFromHcmutSystem(data);
            String roleName = userInfo.getRole().getName().toUpperCase();
            Integer userID = userInfo.getId();
            return jwtHelper.generateToken(String.valueOf(userID), roleName);
        } else {
            throw new MethodNotAllowExceptions("Invalid password for user with email: " + email);
        }
    }
}
