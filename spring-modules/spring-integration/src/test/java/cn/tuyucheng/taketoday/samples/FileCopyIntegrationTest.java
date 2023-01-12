package cn.tuyucheng.taketoday.samples;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class FileCopyIntegrationTest {

	//

	@Test
	public void whenFileCopyConfiguration_thanFileCopiedSuccessfully() throws InterruptedException {
		final AbstractApplicationContext context = new AnnotationConfigApplicationContext(FileCopyConfig.class.getCanonicalName());
		context.registerShutdownHook();
		Thread.sleep(5000);
	}

	@Test
	public void publish() throws InterruptedException {
		final AbstractApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/integration/spring-integration-file-publish-context.xml");

		Thread.sleep(15000);
	}

}
