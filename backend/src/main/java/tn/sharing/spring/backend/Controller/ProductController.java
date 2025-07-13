package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.Product;
import tn.sharing.spring.backend.Entity.ProductTestAttribute;
import tn.sharing.spring.backend.Service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/{userId}")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, @PathVariable Long userId) {
        Product created = productService.addProduct(product, userId);
        if (created == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        Product updated = productService.updateProduct(productId, product);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        if (productService.deleteProduct(productId))
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Optional<Product> product = productService.getProductById(productId);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{productId}/attributes/{userId}")
    public ResponseEntity<ProductTestAttribute> createAttributesForProduct(
            @PathVariable Long productId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> attributes) {
        ProductTestAttribute attr = productService.createAttributesForProduct(productId, userId, attributes);
        if (attr == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(attr);
    }
}
