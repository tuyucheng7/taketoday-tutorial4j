package cn.tuyucheng.taketoday.conditionalonproperty;

import cn.tuyucheng.taketoday.conditionalonproperty.config.NotificationConfig;
import cn.tuyucheng.taketoday.conditionalonproperty.service.EmailNotification;
import cn.tuyucheng.taketoday.conditionalonproperty.service.NotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationUnitTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	void whenValueSetToEmail_thenCreateEmailNotification() {
		this.contextRunner.withPropertyValues("notification.service=email")
			.withUserConfiguration(NotificationConfig.class)
			.run(context -> {
				assertThat(context).hasBean("emailNotification");
				NotificationSender notificationSender = context.getBean(EmailNotification.class);
				assertThat(notificationSender.send("Hello From Tuyucheng!")).isEqualTo("Email Notification: Hello From Tuyucheng!");
				assertThat(context).doesNotHaveBean("smsNotification");
			});
	}
}