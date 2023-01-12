package cn.tuyucheng.taketoday.setterdi;

import cn.tuyucheng.taketoday.setterdi.domain.Engine;
import cn.tuyucheng.taketoday.setterdi.domain.Trailer;
import cn.tuyucheng.taketoday.setterdi.domain.Transmission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.setterdi")
public class Config {

	@Bean
	public Engine engine() {
		Engine engine = new Engine();
		engine.setType("v8");
		engine.setVolume(5);
		return engine;
	}

	@Bean
	public Transmission transmission() {
		Transmission transmission = new Transmission();
		transmission.setType("sliding");
		return transmission;
	}

	@Bean
	public Trailer trailer() {
		Trailer trailer = new Trailer();
		return trailer;
	}
}