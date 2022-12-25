package cn.tuyucheng.taketoday.configurationproperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DatabaseConfigPropertiesApplication.class)
@TestPropertySource("classpath:database-test.properties")
class DatabasePropertiesIntegrationTest {

	@Autowired
	@Qualifier("dataSource")
	private Database database;

	@Test
	void testDatabaseProperties() {
		assertNotNull(database);
		assertEquals("jdbc:postgresql:/localhost:5432", database.getUrl());
		assertEquals("foo", database.getUsername());
		assertEquals("bar", database.getPassword());
	}
}