package tn.sharing.spring.internshipprojectsharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.internshipprojectsharing.Entity.RefreshToken;
import tn.sharing.spring.internshipprojectsharing.Entity.Users;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(Users user);
}
