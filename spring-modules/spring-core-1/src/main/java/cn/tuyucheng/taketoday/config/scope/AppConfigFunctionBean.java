package cn.tuyucheng.taketoday.config.scope;

import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import cn.tuyucheng.taketoday.scope.singletone.SingletonFunctionBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.function.Function;

@Configuration
public class AppConfigFunctionBean {

	@Bean
	public Function<String, PrototypeBean> beanFactory() {
		return name -> prototypeBeanWithParam(name);
	}

	@Bean
	@Scope(value = "prototype")
	public PrototypeBean prototypeBeanWithParam(String name) {
		return new PrototypeBean(name);
	}

	@Bean
	public SingletonFunctionBean singletonFunctionBean() {
		return new SingletonFunctionBean();
	}

}
