package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByHcmutId(String hcmutId);
}
