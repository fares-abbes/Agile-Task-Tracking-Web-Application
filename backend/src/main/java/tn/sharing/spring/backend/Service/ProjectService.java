
package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.*;
import tn.sharing.spring.backend.Repository.ProjectRepo;
import tn.sharing.spring.backend.Repository.TestReportRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final TestReportRepo testReportRepo;


   


    private boolean isQualityUser(int userId) {
        Optional<Users> userOpt = userRepo.findById(userId);
        return userOpt.isPresent() && userOpt.get().getRole() == Role.TESTER;
    }

    public Project addProduct(Project product, int userId) {
        if (!isQualityUser(userId))
            return null;
        return projectRepo.save(product);
    }

    public boolean deleteProduct(int productId) {
        if (projectRepo.existsById(productId)) {
            projectRepo.deleteById(productId);
            return true;
        }
        return false;
    }

    public Project updateProduct(int productId, Project productDetails) {
        return projectRepo.findById(productId).map(product -> {

            product.setTasks(productDetails.getTasks());
            product.setStatus(productDetails.getStatus());
            product.setLastModified(productDetails.getLastModified());
            product.setUser(productDetails.getUser());
            return projectRepo.save(product);
        }).orElse(null);
    }

    public List<Project> getAllProducts() {
        return projectRepo.findAll();
    }

    public Optional<Project> getProductById(int productId) {
        return projectRepo.findById(productId);
    }
}
