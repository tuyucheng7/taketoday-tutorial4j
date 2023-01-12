package cn.tuyucheng.taketoday.constructordi;

import cn.tuyucheng.taketoday.constructordi.domain.Engine;
import cn.tuyucheng.taketoday.constructordi.domain.Transmission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.constructordi")
public class Config {

	@Bean
	public Engine engine() {
		return new Engine("v8", 5);
	}

	@Bean
	public Transmission transmission() {
		return new Transmission("sliding");
	}
}
