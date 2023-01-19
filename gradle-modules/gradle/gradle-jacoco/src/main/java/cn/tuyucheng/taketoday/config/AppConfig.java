package cn.tuyucheng.taketoday.config;

import cn.tuyucheng.taketoday.service.ProductService;

public class AppConfig {

	public ProductService productService() {
		return new ProductService();
	}
}