package cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.configuration;

import cn.tuyucheng.taketoday.dddhexagonalspring.DomainLayerApplication;
import cn.tuyucheng.taketoday.dddhexagonalspring.domain.repository.OrderRepository;
import cn.tuyucheng.taketoday.dddhexagonalspring.domain.service.DomainOrderService;
import cn.tuyucheng.taketoday.dddhexagonalspring.domain.service.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = DomainLayerApplication.class)
public class BeanConfiguration {

	@Bean
	OrderService orderService(final OrderRepository orderRepository) {
		return new DomainOrderService(orderRepository);
	}
}
