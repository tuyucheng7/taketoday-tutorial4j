package cn.tuyucheng.taketoday.javaxval.methodvalidation;

import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.Customer;
import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.Reservation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.time.LocalDate;

@Configuration
@ComponentScan({"cn.tuyucheng.taketoday.javaxval.methodvalidation.model"})
public class MethodValidationConfig {

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Bean("customer")
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public Customer customer(String firstName, String lastName) {

		Customer customer = new Customer(firstName, lastName);
		return customer;
	}

	@Bean("reservation")
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public Reservation reservation(LocalDate begin, LocalDate end, Customer customer, int room) {

		Reservation reservation = new Reservation(begin, end, customer, room);
		return reservation;
	}
}
