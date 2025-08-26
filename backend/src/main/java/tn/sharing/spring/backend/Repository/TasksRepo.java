package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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



    // find tasks assigned to a single user
    List<Tasks> findByUsers_Id(int userId);

    // find tasks for projects where the project's team lead has given id
    List<Tasks> findByProject_TeamLead_Id(int teamLeadId);

    // find tasks assigned to any of the users in the list with given status
    List<Tasks> findByUsers_IdInAndStatus(List<Integer> userIds, Status status);

    // new: allow querying for multiple statuses (DONE + APPROVED)
    List<Tasks> findByUsers_IdInAndStatusIn(List<Integer> userIds, List<Status> statuses);

    @Query("SELECT u.id, u.email, u.team.teamId, COUNT(t) FROM Tasks t JOIN t.users u " +
           "WHERE t.status = tn.sharing.spring.backend.Entity.Status.DONE " +
           "AND MONTH(t.dateFin) = MONTH(CURRENT_DATE) AND YEAR(t.dateFin) = YEAR(CURRENT_DATE) " +
           "GROUP BY u.id, u.email, u.team.teamId " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> rankTeamMembersByTasksDoneThisMonth();

    // optional optimized query:
    List<Tasks> findByProject_ProjectIdAndStatusIn(int projectId, List<Status> statuses);


    /*
      NOTE: If your Tasks entity doesn't have an assignedTo -> team relationship,
      and you want to count tasks belonging to projects owned by a team lead or project.team_id,
      you can use a query like:

      @Query("SELECT COUNT(t) FROM Tasks t WHERE t.project.team.teamId = :teamId AND t.status IN :statuses")
      long countByProjectTeamAndStatuses(@Param("teamId") int teamId, @Param("statuses") List<Status> statuses);
    */

    // Count tasks where the task's project is led by the given teamLead and status is in the list
    @Query("SELECT COUNT(t) FROM Tasks t WHERE t.project.teamLead.id = :teamLeadId AND t.status IN :statuses")
    long countByProject_TeamLead_IdAndStatusIn(@Param("teamLeadId") int teamLeadId, @Param("statuses") List<Status> statuses);

    // Count tasks where the task's project is led by the given teamLead and importance is in the list
    @Query("SELECT COUNT(t) FROM Tasks t WHERE t.project.teamLead.id = :teamLeadId AND t.importance IN :importances")
    long countByProject_TeamLead_IdAndImportanceIn(@Param("teamLeadId") int teamLeadId, @Param("importances") List<Importance> importances);

    // find all tasks for projects led by a team lead excluding a specific status (e.g. APPROVED)
    List<Tasks> findByProject_TeamLead_IdAndStatusNot(int teamLeadId, Status status);
}
