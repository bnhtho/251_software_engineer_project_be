package org.example.axon.repository;

import org.example.axon.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Optional<Doctor> findByUserId(String userId);

    boolean existsByUserId(String userId);
    @Query("""
    select distinct d
    from Doctor d
    join fetch d.users u
    left join fetch d.hospitalDepartment hd
    left join fetch hd.hospital h
    left join fetch hd.department dep
    left join fetch d.doctorAvailabilities av
    where d.userId = :userId
""")
    Optional<Doctor> findDetailByUserId(@Param("userId") String userId);
}
