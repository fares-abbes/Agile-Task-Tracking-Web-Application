package tn.sharing.spring.internshipprojectsharing.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.internshipprojectsharing.Repository.TestReportRepo;
import tn.sharing.spring.internshipprojectsharing.Repository.ProductRepo;
import tn.sharing.spring.internshipprojectsharing.Repository.UserRepo;
import tn.sharing.spring.internshipprojectsharing.Repository.ProductTestAttributeRepo;
import tn.sharing.spring.internshipprojectsharing.Entity.TestReport;
import tn.sharing.spring.internshipprojectsharing.Entity.Product;
import tn.sharing.spring.internshipprojectsharing.Entity.Users;
import tn.sharing.spring.internshipprojectsharing.Entity.Role;
import tn.sharing.spring.internshipprojectsharing.Entity.ProductTestAttribute;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TestReportService {
    private final TestReportRepo testReportRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final ProductTestAttributeRepo productTestAttributeRepo;

    private boolean isQualityUser(Long userId) {
        Optional<Users> userOpt = userRepo.findById(userId);
        return userOpt.isPresent() && userOpt.get().getRole() == Role.QUALITY;
    }

    public TestReport createTestReport(Long productId, Long userId, TestReport report) {
        if (!isQualityUser(userId))
            return null;
        Optional<Product> productOpt = productRepo.findById(productId);
        Optional<Users> userOpt = userRepo.findById(userId);
        if (productOpt.isPresent() && userOpt.isPresent()) {
            report.setProduct(productOpt.get());
            report.setTester(userOpt.get());
            return testReportRepo.save(report);
        }
        return null;
    }

    public List<ProductTestAttribute> getProductTestAttributes(Long productId, Long userId) {
        if (!isQualityUser(userId))
            return null;
        return productTestAttributeRepo.findByProduct_ProductId(productId);
    }

    public ProductTestAttribute setProductTestAttributes(Long productId, Long userId, Map<String, String> attributes) {
        if (!isQualityUser(userId))
            return null;
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isPresent()) {
            ProductTestAttribute attr = new ProductTestAttribute();
            attr.setProduct(productOpt.get());
            attr.setAttributes(attributes);
            return productTestAttributeRepo.save(attr);
        }
        return null;
    }
}
