package cn.tuyucheng.taketoday.spring.jndi.datasource.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleJNDIUnitTest {

	private InitialContext initContext;

	@BeforeEach
	void setup() throws Exception {
		this.initContext = new InitialContext();
	}

	@Test
	void whenMockJndiDataSource_thenReturnJndiDataSource() throws Exception {
		String dsString = "org.h2.Driver::::jdbc:h2:mem:testdb::::sa";
		Context envContext = (Context) this.initContext.lookup("java:/comp/env");
		DataSource ds = (DataSource) envContext.lookup("datasource/ds");

		assertEquals(dsString, ds.toString());
	}

	@AfterEach
	void tearDown() throws Exception {
		if (this.initContext != null) {
			this.initContext.close();
			this.initContext = null;
		}
	}
}