package cn.tuyucheng.taketoday.jasypt;

import cn.tuyucheng.taketoday.jasypt.starter.PropertyServiceForJasyptStarter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JasyptWithStarterIntegrationTest {

	@Autowired
	ApplicationContext appCtx;

	@Test
	void whenDecryptedPasswordNeeded_GetFromService() {
		System.setProperty("jasypt.encryptor.password", "password");
		PropertyServiceForJasyptStarter service = appCtx.getBean(PropertyServiceForJasyptStarter.class);
		assertEquals("Password@1", service.getProperty());

		Environment environment = appCtx.getBean(Environment.class);
		assertEquals("Password@1", service.getPasswordUsingEnvironment(environment));
	}
}