package cn.tuyucheng.taketoday.ecommerce.controller;

import cn.tuyucheng.taketoday.ecommerce.model.Product;
import cn.tuyucheng.taketoday.ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/products")
public class ProductController {

   private final ProductService productService;

   public ProductController(ProductService productService) {
      this.productService = productService;
   }

   @GetMapping(value = {"", "/"})
   public @NotNull Iterable<Product> getProducts() {
      return productService.getAllProducts();
   }
}