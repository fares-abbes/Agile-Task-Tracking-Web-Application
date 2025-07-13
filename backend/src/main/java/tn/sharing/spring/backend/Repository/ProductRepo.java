package tn.sharing.spring.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.backend.Entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
}
