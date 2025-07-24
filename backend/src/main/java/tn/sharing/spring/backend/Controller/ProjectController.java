package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.Project;
import tn.sharing.spring.backend.Service.ProjectService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")

@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService productService;

    @PostMapping("/{userId}")
    public ResponseEntity<Project> addProduct(@RequestBody Project product, @PathVariable int userId) {
        Project created = productService.addProduct(product, userId);
        if (created == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Project> updateProduct(@PathVariable int productId, @RequestBody Project product) {
        Project updated = productService.updateProduct(productId, product);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int productId) {
        if (productService.deleteProduct(productId))
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Project> getProductById(@PathVariable int productId) {
        Optional<Project> product = productService.getProductById(productId);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


}
