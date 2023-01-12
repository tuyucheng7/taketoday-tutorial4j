package cn.tuyucheng.taketoday.scope.singletone;

import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

public class SingletonFunctionBean {

	@Autowired
	private Function<String, PrototypeBean> beanFactory;

	public PrototypeBean getPrototypeInstance(String name) {
		PrototypeBean bean = beanFactory.apply(name);
		return bean;
	}

}
