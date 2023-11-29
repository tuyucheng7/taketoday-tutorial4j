package cn.tuyucheng.taketoday.spring.cloud.connectors.heroku.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

   private final ProductService productService;

   @Autowired
   public ProductController(ProductService productService) {
      this.productService = productService;
   }

   @GetMapping("/{productId}")
   public Optional<Product> findProduct(@PathVariable Long productId) {
      return productService.findProductById(productId);
   }

   @PostMapping
   public Product createProduct(@RequestBody Product product) {
      return productService.createProduct(product);
   }
}