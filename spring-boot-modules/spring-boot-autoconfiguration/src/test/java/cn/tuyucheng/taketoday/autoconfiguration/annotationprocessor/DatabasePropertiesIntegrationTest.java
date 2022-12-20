package cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AnnotationProcessorApplication.class)
@TestPropertySource("classpath:databaseproperties-test.properties")
class DatabasePropertiesIntegrationTest {

	@Autowired
	private DatabaseProperties databaseProperties;

	@Test
	void whenSimplePropertyQueriedThenReturnsPropertyValue() throws Exception {
		assertEquals("tuyucheng", databaseProperties.getUsername(), "Incorrectly bound Username property");
		assertEquals("password", databaseProperties.getPassword(), "Incorrectly bound Password property");
	}

	@Test
	void whenNestedPropertyQueriedThenReturnsPropertyValue() throws Exception {
		assertEquals("127.0.0.1", databaseProperties.getServer().getIp(), "Incorrectly bound Server IP nested property");
		assertEquals(3306, databaseProperties.getServer().getPort(), "Incorrectly bound Server Port nested property");
	}
}