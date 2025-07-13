package tn.sharing.spring.internshipprojectsharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.internshipprojectsharing.Entity.Users;

import java.util.Optional;

@Repository
public interface UserRepo  extends JpaRepository<Users,Long> {

        Optional<Users> findByUsername(String username);


}
