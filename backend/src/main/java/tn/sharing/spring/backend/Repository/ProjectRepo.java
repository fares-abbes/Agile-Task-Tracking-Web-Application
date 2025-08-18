package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.Project;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Integer> {
    List<Project> findByTeamLead_Id(int teamLeadId);

}
