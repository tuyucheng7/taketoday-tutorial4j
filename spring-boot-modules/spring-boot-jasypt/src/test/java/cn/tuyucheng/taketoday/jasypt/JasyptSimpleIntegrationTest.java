package cn.tuyucheng.taketoday.jasypt;

import cn.tuyucheng.taketoday.jasypt.simple.PropertyServiceForJasyptSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JasyptSimpleIntegrationTest {

	@Autowired
	ApplicationContext appCtx;

	@Test
	void whenDecryptedPasswordNeeded_GetFromService() {
		System.setProperty("jasypt.encryptor.password", "password");
		PropertyServiceForJasyptSimple service = appCtx.getBean(PropertyServiceForJasyptSimple.class);

		assertEquals("Password@2", service.getProperty());
	}
}