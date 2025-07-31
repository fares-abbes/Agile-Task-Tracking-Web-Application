package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.TestReport;

@Repository
public interface TestReportRepo extends JpaRepository<TestReport, Integer> {
    // Find test report by task ID
    TestReport findByTask_TaskId(int taskId);
    
    // Delete test report by ID
    void deleteById(Integer reportId);
}
