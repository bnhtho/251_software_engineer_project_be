package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

    Optional<TutorProfile> findByUserId(Long userId);
@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
}

