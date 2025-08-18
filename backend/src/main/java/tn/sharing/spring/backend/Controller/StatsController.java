package tn.sharing.spring.backend.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.DTOs.UserTaskCountDTO;
import tn.sharing.spring.backend.Entity.Status;
import tn.sharing.spring.backend.Service.ClientService;
import tn.sharing.spring.backend.Service.ProjectService;
import tn.sharing.spring.backend.Service.TeamService;
import tn.sharing.spring.backend.Service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatsController {
    private final UserService userService;
    private final TeamService teamService;
    private final ClientService clientService;
    private final ProjectService projectService;

    @GetMapping("/totals")
    public Map<String, Long> getTotals() {
        Map<String, Long> totals = new HashMap<>();
        totals.put("users", userService.getTotalUsers());
        totals.put("teams", teamService.getTotalTeams());
        totals.put("clients", clientService.getTotalClients());
        totals.put("projects", projectService.getTotalProjects());
        return totals;
    }

      @GetMapping("/tasks-per-user")
    public ResponseEntity<List<UserTaskCountDTO>> tasksPerUserInTeam(
            @RequestParam int teamId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(userService.getTaskCountsForTeam(teamId, status, month, year));
    }
}