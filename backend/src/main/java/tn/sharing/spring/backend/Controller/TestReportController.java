package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.TestReport;
import tn.sharing.spring.backend.Service.TestReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/test-reports")
@CrossOrigin(origins = "*")

@RequiredArgsConstructor
public class TestReportController {
    private final TestReportService testReportService;

    @PostMapping("/create/{productId}/{userId}")
    public ResponseEntity<TestReport> createTestReport(@PathVariable int productId, @PathVariable int userId,
                                                       @RequestBody TestReport report) {
        TestReport created = testReportService.createTestReport(productId, userId, report);
        if (created == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(created);
    }
}
