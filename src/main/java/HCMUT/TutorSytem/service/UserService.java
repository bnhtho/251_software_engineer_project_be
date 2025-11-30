package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.model.Datacore;
import HCMUT.TutorSytem.model.User;

public interface UserService {
    User getInfoFromHcmutSystem(Datacore data);
}
