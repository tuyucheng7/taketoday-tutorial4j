package cn.tuyucheng.taketoday.scope;

import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import cn.tuyucheng.taketoday.scope.singletone.SingletonLookupBean;
import cn.tuyucheng.taketoday.scope.singletone.SingletonObjectFactoryBean;
import cn.tuyucheng.taketoday.scope.singletone.SingletonProviderBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertNotSame;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = AppConfig.class)
class PrototypeBeanInjectionIntegrationTest {

	@Test
	void givenPrototypeInjection_WhenObjectFactory_ThenNewInstanceReturn() {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		SingletonObjectFactoryBean firstContext = context.getBean(SingletonObjectFactoryBean.class);
		SingletonObjectFactoryBean secondContext = context.getBean(SingletonObjectFactoryBean.class);

		PrototypeBean firstInstance = firstContext.getPrototypeInstance();
		PrototypeBean secondInstance = secondContext.getPrototypeInstance();

		assertNotSame(firstInstance, secondInstance, "New instance expected");
	}

	@Test
	void givenPrototypeInjection_WhenLookup_ThenNewInstanceReturn() {

		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		SingletonLookupBean firstContext = context.getBean(SingletonLookupBean.class);
		SingletonLookupBean secondContext = context.getBean(SingletonLookupBean.class);

		PrototypeBean firstInstance = firstContext.getPrototypeBean();
		PrototypeBean secondInstance = secondContext.getPrototypeBean();

		assertNotSame(firstInstance, secondInstance, "New instance expected");
	}

	@Test
	void givenPrototypeInjection_WhenProvider_ThenNewInstanceReturn() {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		SingletonProviderBean firstContext = context.getBean(SingletonProviderBean.class);
		SingletonProviderBean secondContext = context.getBean(SingletonProviderBean.class);

		PrototypeBean firstInstance = firstContext.getPrototypeInstance();
		PrototypeBean secondInstance = secondContext.getPrototypeInstance();

		assertNotSame(firstInstance, secondInstance, "New instance expected");
	}
}