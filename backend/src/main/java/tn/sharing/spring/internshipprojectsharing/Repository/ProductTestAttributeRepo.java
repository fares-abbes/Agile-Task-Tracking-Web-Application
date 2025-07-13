package tn.sharing.spring.internshipprojectsharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.internshipprojectsharing.Entity.ProductTestAttribute;

import java.util.List;

@Repository
public interface ProductTestAttributeRepo extends JpaRepository<ProductTestAttribute, Long> {
    List<ProductTestAttribute> findByProduct_ProductId(Long productId);
}
