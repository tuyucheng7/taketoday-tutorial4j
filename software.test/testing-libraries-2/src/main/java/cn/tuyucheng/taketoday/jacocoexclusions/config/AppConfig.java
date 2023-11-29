package cn.tuyucheng.taketoday.jacocoexclusions.config;

import cn.tuyucheng.taketoday.jacocoexclusions.service.ProductService;

public class AppConfig {

   public ProductService productService() {
      return new ProductService();
   }
}