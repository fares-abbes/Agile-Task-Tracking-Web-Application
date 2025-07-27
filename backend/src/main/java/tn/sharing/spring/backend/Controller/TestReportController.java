package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.TestReport;
import tn.sharing.spring.backend.Service.TestReportService;
import tn.sharing.spring.backend.Entity.Tasks;
import tn.sharing.spring.backend.Service.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-reports")
@CrossOrigin(origins = "*")

@RequiredArgsConstructor
public class TestReportController {
    private final TestReportService testReportService;
    private final TaskService taskService;

    @PostMapping("/create-task-report")
    public ResponseEntity<?> createTestReportForTask(
            @RequestParam("teamLeadId") int teamLeadId,
            @RequestParam("taskId") int taskId,
            @RequestBody TestReport testReport) {

        TestReport createdReport = testReportService.createTestReportForTask(teamLeadId, taskId, testReport);

        if (createdReport == null) {
            return ResponseEntity.badRequest()
                    .body("Failed to create test report. Ensure the team lead is authorized and the task exists.");
        }

        return ResponseEntity.ok(createdReport);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getTestReportForTask(@PathVariable int taskId) {
        TestReport report = testReportService.getTestReportForTask(taskId);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(report);
    }


}
