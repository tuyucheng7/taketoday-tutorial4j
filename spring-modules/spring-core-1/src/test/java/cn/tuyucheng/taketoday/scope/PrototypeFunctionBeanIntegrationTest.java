package cn.tuyucheng.taketoday.scope;

import cn.tuyucheng.taketoday.config.scope.AppConfigFunctionBean;
import cn.tuyucheng.taketoday.scope.prototype.PrototypeBean;
import cn.tuyucheng.taketoday.scope.singletone.SingletonFunctionBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = AppConfigFunctionBean.class)
public class PrototypeFunctionBeanIntegrationTest {

	@Test
	public void givenPrototypeInjection_WhenFunction_ThenNewInstanceReturn() {

		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfigFunctionBean.class);

		SingletonFunctionBean firstContext = context.getBean(SingletonFunctionBean.class);
		SingletonFunctionBean secondContext = context.getBean(SingletonFunctionBean.class);

		PrototypeBean firstInstance = firstContext.getPrototypeInstance("instance1");
		PrototypeBean secondInstance = secondContext.getPrototypeInstance("instance2");

		Assert.assertTrue("New instance expected", firstInstance != secondInstance);
	}

}
