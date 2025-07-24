package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Repository.TestReportRepo;
import tn.sharing.spring.backend.Repository.ProjectRepo;
import tn.sharing.spring.backend.Repository.UserRepo;
import tn.sharing.spring.backend.Entity.TestReport;
import tn.sharing.spring.backend.Entity.Project;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Entity.Role;
import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TestReportService {
    private final TestReportRepo testReportRepo;
    private final ProjectRepo productRepo;
    private final UserRepo userRepo;

    private boolean isTester(int userId) {
        Optional<Users> userOpt = userRepo.findById(userId);
        return userOpt.isPresent() && userOpt.get().getRole() == Role.TESTER;
    }
    public TestReport assignTestReportToTester(int reportId, int qualityUserId, int testerId) {
        if (!isTester(qualityUserId))
            return null;
        Optional<TestReport> reportOpt = testReportRepo.findById(reportId);
        Optional<Users> testerOpt = userRepo.findById(testerId);
        if (reportOpt.isPresent() && testerOpt.isPresent()) {
            TestReport report = reportOpt.get();
            report.setTester(testerOpt.get());
            report.setDate(LocalDate.now());
            return testReportRepo.save(report);
        }
        return null;
    }
    public TestReport createTestReport(int productId, int userId, TestReport report) {
        if (!isTester(userId))
            return null;
        Optional<Project> productOpt = productRepo.findById(productId);
        Optional<Users> userOpt = userRepo.findById(userId);
        if (productOpt.isPresent() && userOpt.isPresent()) {
            report.setProduct(productOpt.get());
            return testReportRepo.save(report);
        }
        return null;
    }




}
