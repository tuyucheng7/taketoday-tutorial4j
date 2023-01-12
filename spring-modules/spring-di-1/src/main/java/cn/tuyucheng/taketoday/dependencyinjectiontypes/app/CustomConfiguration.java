package cn.tuyucheng.taketoday.dependencyinjectiontypes.app;

import cn.tuyucheng.taketoday.dependencyinjectiontypes.annotation.CarQualifier;
import cn.tuyucheng.taketoday.dependencyinjectiontypes.model.Car;
import cn.tuyucheng.taketoday.dependencyinjectiontypes.model.CarHandler;
import cn.tuyucheng.taketoday.dependencyinjectiontypes.model.Motorcycle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("cn.tuyucheng.taketoday.dependencyinjectiontypes.model")
public class CustomConfiguration {
	@Bean
	@CarQualifier
	public Car getMercedes() {
		return new Car("E280", "Mercedes", "Diesel");
	}

	public static void main(String[] args) throws NoSuchFieldException {
		ConfigurableApplicationContext context = SpringApplication.run(CustomConfiguration.class, args);
		CarHandler carHandler = context.getBean(CarHandler.class);
		carHandler.getVehicles().forEach(System.out::println);
	}

	@Bean
	@CarQualifier
	public Car getBmw() {
		return new Car("M5", "BMW", "Petrol");
	}

	@Bean
	public Motorcycle getSuzuki() {
		return new Motorcycle("Yamaguchi", "Suzuki", true);
	}
}
