package HCMUT.TutorSytem.repo;

import HCMUT.TutorSytem.model.TutorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Integer> {

    Optional<TutorProfile> findByUserId(Integer userId);

    Page<TutorProfile> findByRegistrationStatusId(Byte statusId, Pageable pageable);

}
