package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.GeneratedProject;

import java.util.List;

@Repository
public interface GeneratedProjectRepo extends JpaRepository<GeneratedProject, Long> {
    List<GeneratedProject> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<GeneratedProject> findByTeamIdOrderByCreatedAtDesc(Long teamId);
}