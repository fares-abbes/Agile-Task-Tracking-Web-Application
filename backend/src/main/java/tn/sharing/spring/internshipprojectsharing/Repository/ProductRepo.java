package tn.sharing.spring.internshipprojectsharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.sharing.spring.internshipprojectsharing.Entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
}
