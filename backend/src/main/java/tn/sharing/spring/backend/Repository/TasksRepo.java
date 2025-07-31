package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.Importance;
import tn.sharing.spring.backend.Entity.Role;
import tn.sharing.spring.backend.Entity.Status;
import tn.sharing.spring.backend.Entity.Tasks;

import java.util.List;

@Repository
public interface TasksRepo extends JpaRepository<Tasks, Integer> {

    List<Tasks> findByProject_ProjectId(int projectId);

    List<Tasks> findByUsers_IdAndStatus(int userId, Status status);

    List<Tasks> findByUsers_IdAndImportance(int userId, Importance importance);
    List<Tasks> findByProject_TeamLead_Id(int teamLeadId);
List<Tasks> findByUsers_Id(int userId);

}
