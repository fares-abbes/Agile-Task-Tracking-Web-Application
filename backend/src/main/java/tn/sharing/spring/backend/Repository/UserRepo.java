package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.DTOs.UserTaskCountDTO;
import tn.sharing.spring.backend.Entity.Role;
import tn.sharing.spring.backend.Entity.Status;
import tn.sharing.spring.backend.Entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByUsername(String username);

    @Query("""
           SELECT new tn.sharing.spring.backend.DTOs.UserTaskCountDTO(
             u.id, u.email, u.team.teamId, COUNT(t)
           )
           FROM Users u
           LEFT JOIN u.tasks t
             ON (:status IS NULL OR t.status = :status)
            AND (:month IS NULL OR MONTH(t.dateFin) = :month)
            AND (:year  IS NULL OR YEAR(t.dateFin)  = :year)
           WHERE u.team.teamId = :teamId
           GROUP BY u.id, u.email, u.team.teamId
           ORDER BY COUNT(t) DESC
           """)
    List<UserTaskCountDTO> countTasksPerUserInTeam(@Param("teamId") int teamId,
                                                   @Param("status") Status status,
                                                   @Param("month") Integer month,
                                                   @Param("year") Integer year);

    List<Users> findByTeam_TeamIdAndRole(int teamId, Role role);
}
