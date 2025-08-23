package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Repository.TasksRepo;
import tn.sharing.spring.backend.Repository.TestReportRepo;
import tn.sharing.spring.backend.Repository.ProjectRepo;
import tn.sharing.spring.backend.Repository.UserRepo;
import tn.sharing.spring.backend.Entity.TestReport;
import tn.sharing.spring.backend.Entity.Project;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Entity.Role;
import tn.sharing.spring.backend.Entity.Tasks;
import java.time.LocalDate;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TestReportService {
    private final TestReportRepo testReportRepo;
    private final ProjectRepo productRepo;
    private final UserRepo userRepo;
    private final TasksRepo tasksRepo;



    public TestReport createTestReportForTask(int taskId, int testerId, TestReport testReport) {
        // Check if the task exists
        Optional<Tasks> taskOpt = tasksRepo.findById(taskId);
        if (taskOpt.isEmpty()) {
            return null;
        }
        Tasks task = taskOpt.get();

        // Check if the tester is valid
        Optional<Users> testerOpt = userRepo.findById(testerId);
        boolean isTesterValid = testerOpt.isPresent() && testerOpt.get().getRole() == Role.TESTER;
        if (!isTesterValid) {
            return null;
        }

        // Set the task and tester, then save the test report
        testReport.setTask(task);
        testReport.setTester(testerOpt.get());
        return testReportRepo.save(testReport);
    }

    /**
     * Get a test report for a specific task
     * 
     * @param taskId the ID of the task
     * @return the test report for the task, or null if not found
     */
    public TestReport getTestReportForTask(int taskId) {
        // Find the task
        Optional<Tasks> taskOpt = tasksRepo.findById(taskId);
        if (taskOpt.isEmpty()) {
            return null;
        }
        
        // Get the test report for this task
        return testReportRepo.findByTask_TaskId(taskId);
    }

    public TestReport addAttributeToTestReport(int reportId, String attributeName, boolean isMet) {
        Optional<TestReport> reportOpt = testReportRepo.findById(reportId);
        if (reportOpt.isEmpty()) {
            return null;
        }
        TestReport report = reportOpt.get();
        if (report.getAttributes() == null) {
            report.setAttributes(new HashMap<>());
        }
        report.getAttributes().put(attributeName, isMet);
        return testReportRepo.save(report);
    }

    public TestReport removeAttributeFromTestReport(int reportId, String attributeName) {
        Optional<TestReport> reportOpt = testReportRepo.findById(reportId);
        if (reportOpt.isEmpty()) {
            return null;
        }
        TestReport report = reportOpt.get();
        if (report.getAttributes() != null) {
            report.getAttributes().remove(attributeName);
            return testReportRepo.save(report);
        }
        return report;
    }

    public TestReport updateIsMetForAttribute(int reportId, String attributeName, boolean isMet) {
        Optional<TestReport> reportOpt = testReportRepo.findById(reportId);
        if (reportOpt.isEmpty()) {
            return null;
        }
        TestReport report = reportOpt.get();
        if (report.getAttributes() != null && report.getAttributes().containsKey(attributeName)) {
            report.getAttributes().put(attributeName, isMet);
            return testReportRepo.save(report);
        }
        return null;
    }

    public List<TestReport> getReportsForDeveloper(int developerId) {
        List<Tasks> tasks = tasksRepo.findByUsers_Id(developerId);
        List<Integer> taskIds = tasks.stream().map(Tasks::getTaskId).toList();
        if (taskIds.isEmpty()) {
            return List.of();
        }
        return testReportRepo.findByTask_TaskIdIn(taskIds);
    }

    public List<TestReport> getReportsForTeamLead(int teamLeadId) {
        // Get all tasks for this team lead
        List<Tasks> tasks = tasksRepo.findByProject_TeamLead_Id(teamLeadId);
        List<Integer> taskIds = tasks.stream().map(Tasks::getTaskId).toList();
        if (taskIds.isEmpty()) {
            return List.of();
        }
        return testReportRepo.findByTask_TaskIdIn(taskIds);
    }

    /**
     * Get test reports assigned to a tester (by testerId).
     * Mirrors the teamLead/developer style: pass only testerId and return all reports assigned to that tester.
     */
    public List<TestReport> getReportsForTester(int testerId) {
        List<TestReport> reports = testReportRepo.findByTester_Id(testerId);
        if (reports == null || reports.isEmpty()) {
            return List.of();
        }
        return reports;
    }
}
