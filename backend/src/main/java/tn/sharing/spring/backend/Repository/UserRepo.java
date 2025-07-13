package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.Users;

import java.util.Optional;

@Repository
public interface UserRepo  extends JpaRepository<Users,Long> {

        Optional<Users> findByUsername(String username);


}
