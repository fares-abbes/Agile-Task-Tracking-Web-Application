// TeamRepo.java
package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.Team;
import java.util.List;

public interface TeamRepo extends JpaRepository<Team, Integer> {


}