package cn.tuyucheng.taketoday.transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootApplication
@ExtendWith(SpringExtension.class)
class TransactionalDetectionUnitTest {

	@Test
	@Transactional
	void givenTransactional_whenCheckingForActiveTransaction_thenReceiveTrue() {
		assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
	}

	@Test
	void givenNoTransactional_whenCheckingForActiveTransaction_thenReceiveFalse() {
		assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
	}
}