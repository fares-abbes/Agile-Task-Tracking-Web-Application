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

@AllArgsConstructor
@Service
public class TestReportService {
    private final TestReportRepo testReportRepo;
    private final ProjectRepo productRepo;
    private final UserRepo userRepo;
    private final TasksRepo tasksRepo;



    public TestReport createTestReportForTask(int teamLeadId, int taskId, int testerId, TestReport testReport) {
        System.out.println("Starting createTestReportForTask: teamLeadId=" + teamLeadId + ", taskId=" + taskId);
        
        // Check if the task exists
        Optional<Tasks> taskOpt = tasksRepo.findById(taskId);
        if (taskOpt.isEmpty()) {
            System.out.println("Task not found with ID: " + taskId);
            return null;
        }

        Tasks task = taskOpt.get();
        System.out.println("Task found: " + task.getTaksName());

       Optional<Users> teamLeadOpt = userRepo.findById(teamLeadId);
boolean isTeamLead = teamLeadOpt.isPresent() && teamLeadOpt.get().getRole() == Role.TEAMLEAD;
        
        System.out.println("Team lead check: " + isTeamLead + " for user ID: " + teamLeadId);
        if (!isTeamLead) {
            System.out.println("User is not authorized as team lead for this task");
            return null;
        }

        // Check if the tester is valid
        Optional<Users> testerOpt = userRepo.findById(testerId);
        boolean isTesterValid = testerOpt.isPresent() && testerOpt.get().getRole() == Role.TESTER;
        System.out.println("Tester check: " + isTesterValid + " for tester ID: " + testerId);
        
        if (!isTesterValid) {
            System.out.println("User is not a valid tester with ID: " + testerId);
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
}
