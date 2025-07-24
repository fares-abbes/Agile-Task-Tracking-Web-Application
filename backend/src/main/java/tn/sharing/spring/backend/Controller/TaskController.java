package tn.sharing.spring.backend.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.DTOs.TaskAssignmentRequest;
import tn.sharing.spring.backend.Entity.Tasks;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tasks>> getTasksByUser(@PathVariable int userId) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Tasks>> getTasksByProject(@PathVariable int projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }
}
