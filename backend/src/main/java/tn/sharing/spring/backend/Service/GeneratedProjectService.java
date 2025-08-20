package tn.sharing.spring.backend.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.GeneratedProject;
import tn.sharing.spring.backend.Repository.GeneratedProjectRepo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneratedProjectService {
    private final GeneratedProjectRepo repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public GeneratedProject saveNew(String title, String description, Object tasks, Long userId, Long teamId) {
        try {
            String tasksJson = mapper.writeValueAsString(tasks);
            GeneratedProject gp = new GeneratedProject();
            gp.setTitle(title);
            gp.setDescription(description);
            gp.setTasksJson(tasksJson);
            gp.setUserId(userId);
            gp.setTeamId(teamId);
            gp.setCreatedAt(Instant.now());
            gp.setUpdatedAt(Instant.now());
            return repo.save(gp);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<GeneratedProject> findById(Long id) {
        return repo.findById(id);
    }

    public GeneratedProject update(Long id, String description, Object tasks) {
        try {
            GeneratedProject gp = repo.findById(id).orElseThrow();
            gp.setDescription(description);
            gp.setTasksJson(mapper.writeValueAsString(tasks));
            gp.setUpdatedAt(Instant.now());
            return repo.save(gp);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<GeneratedProject> historyForUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<GeneratedProject> historyForTeam(Long teamId) {
        return repo.findByTeamIdOrderByCreatedAtDesc(teamId);
    }
}