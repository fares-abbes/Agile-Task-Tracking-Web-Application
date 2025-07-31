package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.TestReport;
import tn.sharing.spring.backend.Repository.TestReportRepo;
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
    private final TestReportRepo testReportRepo;

    @PostMapping("/create-task-report")
    public ResponseEntity<?> createTestReportForTask(
            @RequestParam("taskId") int taskId,
            @RequestParam("testerId") int testerId,
            @RequestBody TestReport testReport) {

        TestReport createdReport = testReportService.createTestReportForTask(taskId, testerId, testReport);

        if (createdReport == null) {
            return ResponseEntity.badRequest()
                    .body("Failed to create test report. Ensure the task exists and the tester is valid.");
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
    
    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteTestReport(@PathVariable int reportId) {
        if (!testReportRepo.existsById(reportId)) {
            return ResponseEntity.notFound().build();
        }
        testReportRepo.deleteById(reportId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reportId}/attributes")
    public ResponseEntity<?> addAttributeToTestReport(
            @PathVariable int reportId,
            @RequestParam String attributeName,
            @RequestParam boolean isMet) {
        TestReport updated = testReportService.addAttributeToTestReport(reportId, attributeName, isMet);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{reportId}/attributes/{attributeName}")
    public ResponseEntity<?> removeAttributeFromTestReport(
            @PathVariable int reportId,
            @PathVariable String attributeName) {
        TestReport updated = testReportService.removeAttributeFromTestReport(reportId, attributeName);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{reportId}/attributes/{attributeName}/isMet")
    public ResponseEntity<?> updateIsMetForAttribute(
            @PathVariable int reportId,
            @PathVariable String attributeName,
            @RequestParam boolean isMet) {
        TestReport updated = testReportService.updateIsMetForAttribute(reportId, attributeName, isMet);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
