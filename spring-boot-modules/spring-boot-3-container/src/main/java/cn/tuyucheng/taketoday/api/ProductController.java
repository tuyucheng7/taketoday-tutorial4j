package cn.tuyucheng.taketoday.api;

import cn.tuyucheng.taketoday.domain.Product;
import cn.tuyucheng.taketoday.domain.ProductEventPublisher;
import cn.tuyucheng.taketoday.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
class ProductController {
   private final ProductRepository productRepository;
   private final ProductEventPublisher productEventPublisher;

   @GetMapping
   public List<Product> getAllProducts() {
      return productRepository.findAll();
   }

   @PostMapping
   public ResponseEntity<Void> createProduct(@RequestBody Product product) {
      productEventPublisher.publishProductCreatedEvent(product);
      return ResponseEntity.ok().build();
   }
}