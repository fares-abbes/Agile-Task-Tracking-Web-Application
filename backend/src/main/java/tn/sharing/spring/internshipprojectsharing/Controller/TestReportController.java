package tn.sharing.spring.internshipprojectsharing.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.internshipprojectsharing.Entity.TestReport;
import tn.sharing.spring.internshipprojectsharing.Service.TestReportService;
import java.util.Map;

@RestController
@RequestMapping("/api/test-reports")
@RequiredArgsConstructor
public class TestReportController {
    private final TestReportService testReportService;

    @PostMapping("/create/{productId}/{userId}")
    public ResponseEntity<TestReport> createTestReport(@PathVariable Long productId, @PathVariable Long userId,
            @RequestBody TestReport report) {
        TestReport created = testReportService.createTestReport(productId, userId, report);
        if (created == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(created);
    }

    @PostMapping("/attributes/{productId}/{userId}")
    public ResponseEntity<?> setProductTestAttributes(@PathVariable Long productId, @PathVariable Long userId,
            @RequestBody Map<String, String> attributes) {
        var attr = testReportService.setProductTestAttributes(productId, userId, attributes);
        if (attr == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(attr);
    }

    @GetMapping("/attributes/{productId}/{userId}")
    public ResponseEntity<?> getProductTestAttributes(@PathVariable Long productId, @PathVariable Long userId) {
        var attrs = testReportService.getProductTestAttributes(productId, userId);
        if (attrs == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(attrs);
    }
}
