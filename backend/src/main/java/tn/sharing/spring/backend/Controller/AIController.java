package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Service.AIService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    @PostMapping("/generate-tasks")
    public ResponseEntity<?> generateTasks(@RequestBody Map<String, String> body) {
        String description = body.get("description");
        List<Map<String, Object>> tasks = aiService.generateTasksFromDescription(description);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/assign-tasks")
    public ResponseEntity<?> assignTasks(@RequestBody Map<String, Object> body) {
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) body.get("tasks");
        int teamId = (int) body.get("teamId");
        List<Map<String, Object>> assignments = aiService.assignTasksToTeam(tasks, teamId);
        return ResponseEntity.ok(assignments);
    }
}
