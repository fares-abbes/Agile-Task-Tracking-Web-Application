package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.ProductTestAttribute;

import java.util.List;

@Repository
public interface ProductTestAttributeRepo extends JpaRepository<ProductTestAttribute, Long> {
    List<ProductTestAttribute> findByProduct_ProductId(Long productId);
}
