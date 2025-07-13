package tn.sharing.spring.internshipprojectsharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.internshipprojectsharing.Entity.TestReport;

@Repository
public interface TestReportRepo extends JpaRepository<TestReport, Long> {
}
