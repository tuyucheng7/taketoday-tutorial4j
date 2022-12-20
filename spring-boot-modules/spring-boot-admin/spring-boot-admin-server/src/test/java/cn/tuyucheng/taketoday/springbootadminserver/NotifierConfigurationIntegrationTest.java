package cn.tuyucheng.taketoday.springbootadminserver;

import cn.tuyucheng.taketoday.springbootadminserver.configs.NotifierConfiguration;
import de.codecentric.boot.admin.server.notify.Notifier;
import de.codecentric.boot.admin.server.notify.RemindingNotifier;
import de.codecentric.boot.admin.server.notify.filter.FilteringNotifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {NotifierConfiguration.class, SpringBootAdminServerApplication.class}, webEnvironment = NONE)
class NotifierConfigurationIntegrationTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void whenApplicationContextStart_ThenNotifierBeanExists() {
		Notifier notifier = (Notifier) applicationContext.getBean("notifier");
		assertNotEquals(null, notifier);
	}

	@Test
	void whenApplicationContextStart_ThenFilteringNotifierBeanExists() {
		FilteringNotifier filteringNotifier = (FilteringNotifier) applicationContext.getBean("filteringNotifier");
		assertNotEquals(null, filteringNotifier);
	}

	@Test
	void whenApplicationContextStart_ThenRemindingNotifierBeanExists() {
		RemindingNotifier remindingNotifier = (RemindingNotifier) applicationContext.getBean("remindingNotifier");
		assertNotEquals(null, remindingNotifier);
	}
}