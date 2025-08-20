package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Service.AIService;
import tn.sharing.spring.backend.Service.GeneratedProjectService;
import tn.sharing.spring.backend.Entity.GeneratedProject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;
    private final GeneratedProjectService gpService;

    // Generate tasks and save history; optional userId/teamId request params
    @PostMapping("/generate-tasks")
    public ResponseEntity<?> generateTasks(@RequestBody Map<String, Object> body,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) Long teamId) {
        String description = (String) body.get("description");
        String title = (String) body.getOrDefault("title", null);
        List<Map<String, Object>> tasks = aiService.generateTasksFromDescription(description);
        // save history
        GeneratedProject gp = gpService.saveNew(title, description, tasks, userId, teamId);
        return ResponseEntity.ok(Map.of("projectId", gp.getId(), "tasks", tasks));
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(@RequestParam(required = false) Long userId,
                                     @RequestParam(required = false) Long teamId) {
        if (userId != null) return ResponseEntity.ok(gpService.historyForUser(userId));
        if (teamId != null) return ResponseEntity.ok(gpService.historyForTeam(teamId));
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getHistory(@PathVariable Long id) {
        Optional<GeneratedProject> gp = gpService.findById(id);
        return gp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Regenerate: re-run AI with saved description, update the record, return updated tasks
    @PutMapping("/history/{id}/regenerate")
    public ResponseEntity<?> regenerate(@PathVariable Long id) {
        Optional<GeneratedProject> gpOpt = gpService.findById(id);
        if (gpOpt.isEmpty()) return ResponseEntity.notFound().build();
        GeneratedProject gp = gpOpt.get();
        List<Map<String, Object>> tasks = aiService.generateTasksFromDescription(gp.getDescription());
        GeneratedProject updated = gpService.update(id, gp.getDescription(), tasks);
        return ResponseEntity.ok(Map.of("projectId", updated.getId(), "tasks", tasks));
    }
}
