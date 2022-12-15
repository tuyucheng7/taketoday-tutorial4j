package cn.tuyucheng.taketoday.scope.singletone;

import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

public class SingletonBean {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PrototypeBean prototypeBean;

	public SingletonBean() {
		logger.info("Singleton instance created");
	}

	public PrototypeBean getPrototypeBean() {
		logger.info(String.valueOf(LocalTime.now()));
		return prototypeBean;
	}
}