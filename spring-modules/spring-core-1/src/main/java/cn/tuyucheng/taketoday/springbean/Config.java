package cn.tuyucheng.taketoday.springbean;

import cn.tuyucheng.taketoday.springbean.domain.Address;
import cn.tuyucheng.taketoday.springbean.domain.Company;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = Company.class)
public class Config {
	@Bean
	public Address getAddress() {
		return new Address("High Street", 1000);
	}
}
