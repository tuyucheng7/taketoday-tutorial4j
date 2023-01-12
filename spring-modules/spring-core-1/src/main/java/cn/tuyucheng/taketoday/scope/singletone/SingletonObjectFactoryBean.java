package cn.tuyucheng.taketoday.scope.singletone;

import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SingletonObjectFactoryBean {

	@Autowired
	private ObjectFactory<PrototypeBean> prototypeBeanObjectFactory;

	public PrototypeBean getPrototypeInstance() {
		return prototypeBeanObjectFactory.getObject();
	}
}
