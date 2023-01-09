package cn.tuyucheng.taketoday.test;

import cn.tuyucheng.taketoday.interceptor.AuditedInterceptor;
import cn.tuyucheng.taketoday.service.SuperService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class InterceptorIntegrationTest {
	Weld weld;
	WeldContainer container;

	@Before
	public void init() {
		weld = new Weld();
		container = weld.initialize();
	}

	@After
	public void shutdown() {
		weld.shutdown();
	}

	@Test
	public void givenTheService_whenMethodAndInterceptorExecuted_thenOK() {
		SuperService superService = container.select(SuperService.class).get();
		String code = "123456";
		superService.deliverService(code);

		Assert.assertTrue(AuditedInterceptor.calledBefore);
		Assert.assertTrue(AuditedInterceptor.calledAfter);
	}

}
