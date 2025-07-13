package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.Product;
import tn.sharing.spring.backend.Entity.ProductTestAttribute;
import tn.sharing.spring.backend.Entity.Role;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Repository.ProductTestAttributeRepo;
import tn.sharing.spring.backend.Repository.ProductRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductTestAttributeService {
    private final ProductTestAttributeRepo productTestAttributeRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    private boolean isQualityUser(Long userId) {
        Optional<Users> userOpt = userRepo.findById(userId);
        return userOpt.isPresent() && userOpt.get().getRole() == Role.QUALITY;
    }

    public ProductTestAttribute createAttributesForProduct(Long productId, Long userId, Map<String, String> attributes) {
        if (!isQualityUser(userId)) return null;
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
