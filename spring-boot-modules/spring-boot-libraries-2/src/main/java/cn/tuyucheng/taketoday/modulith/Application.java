package cn.tuyucheng.taketoday.modulith;

import cn.tuyucheng.taketoday.modulith.product.ProductService;
import cn.tuyucheng.taketoday.modulith.product.internal.Product;
import org.jobrunr.autoconfigure.JobRunrAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableAutoConfiguration(exclude = {JobRunrAutoConfiguration.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args)
			.getBean(ProductService.class)
			.create(new Product("tuyucheng", "course", 10));
	}
}