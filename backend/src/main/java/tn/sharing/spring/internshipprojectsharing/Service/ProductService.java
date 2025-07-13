
package tn.sharing.spring.internshipprojectsharing.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.internshipprojectsharing.Repository.ProductRepo;
import tn.sharing.spring.internshipprojectsharing.Repository.UserRepo;
import tn.sharing.spring.internshipprojectsharing.Entity.Product;
import tn.sharing.spring.internshipprojectsharing.Entity.Users;
import tn.sharing.spring.internshipprojectsharing.Entity.Role;
import tn.sharing.spring.internshipprojectsharing.Entity.ProductTestAttribute;
import tn.sharing.spring.internshipprojectsharing.Repository.ProductTestAttributeRepo;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final ProductTestAttributeRepo productTestAttributeRepo;

    public ProductTestAttribute createAttributesForProduct(Long productId, Long userId,
            Map<String, String> attributes) {
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

    private boolean isQualityUser(Long userId) {
        Optional<Users> userOpt = userRepo.findById(userId);
        return userOpt.isPresent() && userOpt.get().getRole() == Role.QUALITY;
    }

    public Product addProduct(Product product, Long userId) {
        if (!isQualityUser(userId))
            return null;
        return productRepo.save(product);
    }

    public boolean deleteProduct(Long productId) {
        if (productRepo.existsById(productId)) {
            productRepo.deleteById(productId);
            return true;
        }
        return false;
    }

    public Product updateProduct(Long productId, Product productDetails) {
        return productRepo.findById(productId).map(product -> {
            product.setSerialNumber(productDetails.getSerialNumber());
            product.setLocation(productDetails.getLocation());
            product.setInstallationDate(productDetails.getInstallationDate());
            product.setModels(productDetails.getModels());
            product.setStatus(productDetails.getStatus());
            product.setLastModified(productDetails.getLastModified());
            product.setUser(productDetails.getUser());
            return productRepo.save(product);
        }).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepo.findById(productId);
    }
}
