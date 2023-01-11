package cn.tuyucheng.taketoday.lazy_load_no_trans;

import cn.tuyucheng.taketoday.h2db.lazy_load_no_trans.LazyLoadNoTransSpringBootApplication;
import cn.tuyucheng.taketoday.h2db.lazy_load_no_trans.service.ServiceLayer;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LazyLoadNoTransSpringBootApplication.class)
@ActiveProfiles("lazy-load-no-trans-off")
class LazyLoadNoTransPropertyOffIntegrationTest {

	@Autowired
	private ServiceLayer serviceLayer;

	private static final long EXPECTED_DOCS_COLLECTION_SIZE = 6;

	@Test
	void whenCallNonTransactionalMethodWithPropertyOff_thenThrowException() {
		assertThrows(LazyInitializationException.class, () -> serviceLayer.countAllDocsNonTransactional());
	}

	@Test
	void whenCallTransactionalMethodWithPropertyOff_thenTestPass() {
		SQLStatementCountValidator.reset();

		long docsCount = serviceLayer.countAllDocsTransactional();

		assertEquals(EXPECTED_DOCS_COLLECTION_SIZE, docsCount);

		SQLStatementCountValidator.assertSelectCount(2);
	}
}