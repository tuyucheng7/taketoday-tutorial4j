package cn.tuyucheng.taketoday.lazy_load_no_trans;

import cn.tuyucheng.taketoday.h2db.lazy_load_no_trans.LazyLoadNoTransSpringBootApplication;
import cn.tuyucheng.taketoday.h2db.lazy_load_no_trans.service.ServiceLayer;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LazyLoadNoTransSpringBootApplication.class)
@ActiveProfiles("lazy-load-no-trans-on")
class LazyLoadNoTransPropertyOnIntegrationTest {

	@Autowired
	private ServiceLayer serviceLayer;

	private static final long EXPECTED_DOCS_COLLECTION_SIZE = 6;
	private static final long EXPECTED_USERS_COUNT = 5;

	@Test
	void whenCallNonTransactionalMethodWithPropertyOn_thenGetNplusOne() {
		SQLStatementCountValidator.reset();

		long docsCount = serviceLayer.countAllDocsNonTransactional();

		assertEquals(EXPECTED_DOCS_COLLECTION_SIZE, docsCount);

		SQLStatementCountValidator.assertSelectCount(EXPECTED_USERS_COUNT + 1);
	}

	@Test
	void whenCallTransactionalMethodWithPropertyOn_thenTestPass() {
		SQLStatementCountValidator.reset();

		long docsCount = serviceLayer.countAllDocsTransactional();

		assertEquals(EXPECTED_DOCS_COLLECTION_SIZE, docsCount);

		SQLStatementCountValidator.assertSelectCount(2);
	}
}