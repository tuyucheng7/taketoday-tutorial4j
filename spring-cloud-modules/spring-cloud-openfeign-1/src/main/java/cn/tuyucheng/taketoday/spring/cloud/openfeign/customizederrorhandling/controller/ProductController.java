package cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.controller;

import cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.client.ProductClient;
import cn.tuyucheng.taketoday.spring.cloud.openfeign.defaulterrorhandling.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("product_controller2")
@RequestMapping(value = "myapp2")
public class ProductController {

   private final ProductClient productClient;

   @Autowired
   public ProductController(ProductClient productClient) {
      this.productClient = productClient;
   }

   @GetMapping("/product/{id}")
   public Product getProduct(@PathVariable String id) {
      return productClient.getProduct(id);
   }
}