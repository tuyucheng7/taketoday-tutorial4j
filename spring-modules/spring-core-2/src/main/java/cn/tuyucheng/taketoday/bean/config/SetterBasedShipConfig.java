package cn.tuyucheng.taketoday.bean.config;

import cn.tuyucheng.taketoday.bean.injection.Helm;
import cn.tuyucheng.taketoday.bean.injection.Ship;
import org.springframework.context.annotation.Bean;

public class SetterBasedShipConfig {

	@Bean
	public Ship ship() {
		return new Ship();
	}

	@Bean
	public Helm helm() {
		return new Helm();
	}
}
