package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.TestReport;

import java.util.List;


@Repository
public interface TestReportRepo extends JpaRepository<TestReport, Integer> {
    // Find test report by task ID
    TestReport findByTask_TaskId(int taskId);
    
    // Delete test report by ID
    void deleteById(Integer reportId);
    List<TestReport> findByTask_TaskIdIn(List<Integer> taskIds);

    // reports for a given tester
    List<TestReport> findByTester_Id(int testerId);

    // reports for a given tester filtered by task id
    List<TestReport> findByTester_IdAndTask_TaskId(int testerId, int taskId);
}
