package cn.tuyucheng.taketoday.scope;

import cn.tuyucheng.taketoday.config.scope.AppConfigFunctionBean;
import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import cn.tuyucheng.taketoday.scope.singletone.SingletonFunctionBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertNotSame;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = AppConfigFunctionBean.class)
class PrototypeFunctionBeanIntegrationTest {

	@Test
	void givenPrototypeInjection_WhenFunction_ThenNewInstanceReturn() {

		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfigFunctionBean.class);

		SingletonFunctionBean firstContext = context.getBean(SingletonFunctionBean.class);
		SingletonFunctionBean secondContext = context.getBean(SingletonFunctionBean.class);

		PrototypeBean firstInstance = firstContext.getPrototypeInstance("instance1");
		PrototypeBean secondInstance = secondContext.getPrototypeInstance("instance2");

		assertNotSame(firstInstance, secondInstance, "New instance expected");
	}
}