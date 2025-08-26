package tn.sharing.spring.backend.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.DTOs.ProjectProgressDTO;
import tn.sharing.spring.backend.DTOs.TaskAssignmentRequest;
import tn.sharing.spring.backend.DTOs.TaskCreationRequest;
import tn.sharing.spring.backend.DTOs.UserTaskRankDTO;
import tn.sharing.spring.backend.Entity.Tasks;
import tn.sharing.spring.backend.Entity.Status;
import tn.sharing.spring.backend.Entity.Importance;

import tn.sharing.spring.backend.Service.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/teamlead/{teamLeadId}/counts/pending")
    public ResponseEntity<Long> countTeamLeadPending(@PathVariable("teamLeadId") int teamLeadId) {
        try {
            long c = taskService.countPendingByTeamLead(teamLeadId);
            return ResponseEntity.ok(c);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/teamlead/{teamLeadId}/counts/completed")
    public ResponseEntity<Long> countTeamLeadCompleted(@PathVariable("teamLeadId") int teamLeadId) {
        try {
            long c = taskService.countCompletedByTeamLead(teamLeadId);
            return ResponseEntity.ok(c);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/teamlead/{teamLeadId}/counts/high-priority")
    public ResponseEntity<Long> countTeamLeadHighPriority(@PathVariable("teamLeadId") int teamLeadId) {
        try {
            long c = taskService.countHighPriorityByTeamLead(teamLeadId);
            return ResponseEntity.ok(c);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @GetMapping("/developer/{developerId}/importance/{importance}")
    public ResponseEntity<List<Tasks>> getTasksByDeveloperAndImportance(
            @PathVariable int developerId,
            @PathVariable Importance importance) {
        return ResponseEntity.ok(taskService.getTasksByDeveloperAndImportance(developerId, importance));
    }

    @GetMapping("/teamlead/{teamLeadId}")
    public ResponseEntity<List<Tasks>> getTasksByTeamLead(@PathVariable int teamLeadId) {
        List<Tasks> tasks = taskService.getTasksByTeamLead(teamLeadId);
        return ResponseEntity.ok(tasks);
    }
    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<Tasks>> getTasksByDeveloper(@PathVariable int developerId) {
        List<Tasks> tasks = taskService.getTasksByDeveloper(developerId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/assign-to-tester/{teamLeadId}")
    public ResponseEntity<Tasks> assignTaskToTester(
            @PathVariable int teamLeadId,
            @RequestBody TaskAssignmentRequest request) {
        Tasks assignedTask = taskService.assignTaskToTester(teamLeadId, request);
        if (assignedTask == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(assignedTask);
    }

    @GetMapping("/getTasksByTester/{testerId}")
    public ResponseEntity<List<Tasks>> getTasksByTester(@PathVariable int testerId) {
        List<Tasks> tasks = taskService.getTasksByTester(testerId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/tester/{testerId}/task/{taskId}/status/{status}")
    public ResponseEntity<Tasks> updateTaskStatusByTester(
            @PathVariable int testerId,
            @PathVariable int taskId,
            @PathVariable Status status) {

        Tasks updatedTask = taskService.updateTaskStatusByTester(testerId, taskId, status);

        if (updatedTask == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<Tasks>> filterTasksByStatusAndImportance(
            @PathVariable int userId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Importance importance) {

        List<Tasks> userTasks = taskService.getTasksByUserId(userId);
        List<Tasks> filtered = taskService.filterTasksByStatusAndImportance(userTasks, status, importance);
        return ResponseEntity.ok(filtered);
    }

    @PutMapping("/task/{taskId}/status/{status}")
    public ResponseEntity<Tasks> updateTaskStatus(
            @PathVariable int taskId,
            @PathVariable Status status) {
        Tasks updatedTask = taskService.updateTaskStatus(taskId, status);
        if (updatedTask == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Return, for a given team id, the number of tasks marked DONE assigned to each team member.
     * Matches the service method: rankTeamMembersByTasksDoneThisMonth(int teamId)
     */
    @GetMapping("/team/{teamId}/rank-done")
    public ResponseEntity<List<UserTaskRankDTO>> rankTeamMembersByTasksDoneThisMonth(@PathVariable("teamId") int teamId) {
        try {
            List<UserTaskRankDTO> stats = taskService.rankTeamMembersByTasksDoneThisMonth(teamId);
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Return per-project progress (done + approved %) for projects led by the given team lead.
     */
    @GetMapping("/teamlead/{teamLeadId}/project-progress")
    public ResponseEntity<List<ProjectProgressDTO>> getProjectProgressForTeamLead(@PathVariable("teamLeadId") int teamLeadId) {
        try {
            List<ProjectProgressDTO> stats = taskService.getProjectProgressForTeamLead(teamLeadId);
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Return count of team members for the team led by the given teamLeadId (user id).
     */
    @GetMapping("/teamlead/{teamLeadId}/members/count")
    public ResponseEntity<Long> countTeamLeadMembers(@PathVariable("teamLeadId") int teamLeadId) {
        try {
            long count = taskService.countTeamMembersByTeamLead(teamLeadId);
            return ResponseEntity.ok(count);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Return all tasks for projects led by the given teamLeadId except tasks with status APPROVED.
     */
    @GetMapping("/teamlead/{teamLeadId}/tasks/not-approved")
    public ResponseEntity<List<Tasks>> getTeamLeadTasksExcludingApproved(@PathVariable("teamLeadId") int teamLeadId) {
        try {
            List<Tasks> tasks = taskService.getTasksForTeamLeadExcludingApproved(teamLeadId);
            return ResponseEntity.ok(tasks);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
