package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationStatusRepository extends JpaRepository<RegistrationStatus, Byte> {
    Optional<RegistrationStatus> findByName(String name);
}

