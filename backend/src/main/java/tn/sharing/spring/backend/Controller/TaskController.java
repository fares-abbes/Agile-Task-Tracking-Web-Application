package tn.sharing.spring.backend.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.DTOs.TaskAssignmentRequest;
import tn.sharing.spring.backend.DTOs.TaskCreationRequest;
import tn.sharing.spring.backend.Entity.Tasks;
import tn.sharing.spring.backend.Entity.Status;

import tn.sharing.spring.backend.Service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignTask(
            @RequestParam("teamLeadId") int teamLeadId,
            @RequestBody TaskAssignmentRequest request) {

        Tasks task = taskService.assignTaskToDeveloper(teamLeadId, request);

        if (task == null) {
            return ResponseEntity.badRequest()
                    .body("Failed to assign task. Check if team lead, developer and project exist with correct roles.");
        }

        return ResponseEntity.ok(task);
    }
        @GetMapping("/getAllTasks")
    public ResponseEntity<List<Tasks>> getAllTasks() {
        List<Tasks> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tasks>> getTasksByUser(@PathVariable int userId) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Tasks>> getTasksByProject(@PathVariable int projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @RequestParam("teamLeadId") int teamLeadId,
            @RequestParam("projectId") int projectId,
            @RequestBody TaskCreationRequest request) {

        Tasks task = taskService.createTask(teamLeadId, projectId, request);

        if (task == null) {
            return ResponseEntity.badRequest()
                    .body("Failed to create task. Check if team lead and project exist with correct roles.");
        }

        return ResponseEntity.ok(task);
    }

    @GetMapping("/developer/{developerId}/status/{status}")
    public ResponseEntity<List<Tasks>> getTasksByDeveloperAndStatus(
            @PathVariable int developerId,
            @PathVariable Status status) {
        return ResponseEntity.ok(taskService.getTasksByDeveloperAndStatus(developerId, status));
    }
}
