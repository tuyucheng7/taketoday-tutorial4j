package cn.tuyucheng.taketoday.cloud.openfeign.defaulterrorhandling.controller;

import cn.tuyucheng.taketoday.cloud.openfeign.defaulterrorhandling.client.ProductClient;
import cn.tuyucheng.taketoday.cloud.openfeign.defaulterrorhandling.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("product_controller1")
@RequestMapping(value = "myapp1")
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
