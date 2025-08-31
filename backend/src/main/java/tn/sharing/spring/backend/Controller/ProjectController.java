package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.Project;
import tn.sharing.spring.backend.Service.ProjectService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")

@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/add/{userId}/{clientId}")
    public ResponseEntity<Project> addProject(@RequestBody Project project, @PathVariable int userId, @PathVariable int clientId) {
        Project created = projectService.addProject(project, userId, clientId);
        if (created == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable int projectId, @RequestBody Project project) {
        Project updated = projectService.updateProject(projectId, project);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable int projectId) {
        if (projectService.deleteProject(projectId))
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable int projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teamlead/{teamLeadId}")
    public ResponseEntity<List<Project>> getProjectsByTeamLead(@PathVariable int teamLeadId) {
        List<Project> projects = projectService.getProjectsByTeamLead(teamLeadId);
        return ResponseEntity.ok(projects);
    }

}
