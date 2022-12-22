package cn.tuyucheng.taketoday.jasypt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MainApplication.class})
class CustomJasyptIntegrationTest {

	@Autowired
	ApplicationContext appCtx;

	@Test
	void whenConfiguredExcryptorUsed_ReturnCustomEncryptor() {
		Environment environment = appCtx.getBean(Environment.class);

		assertEquals("Password@3", environment.getProperty("encryptedv3.property"));
	}
}