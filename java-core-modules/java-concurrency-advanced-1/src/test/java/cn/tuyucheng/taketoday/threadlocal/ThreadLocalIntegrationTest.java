package cn.tuyucheng.taketoday.threadlocal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadLocalIntegrationTest {

	@Test
	void givenThreadThatStoresContextInAMap_whenStartThread_thenShouldSetContextForBothUsers() throws InterruptedException {
		SharedMapWithUserContext firstUser = new SharedMapWithUserContext(1);
		SharedMapWithUserContext secondUser = new SharedMapWithUserContext(2);
		new Thread(firstUser).start();
		new Thread(secondUser).start();

		Thread.sleep(3000);
		assertEquals(SharedMapWithUserContext.userContextPerUserId.size(), 2);
	}

	@Test
	void givenThreadThatStoresContextInThreadLocal_whenStartThread_thenShouldStoreContextInThreadLocal() throws InterruptedException {
		ThreadLocalWithUserContext firstUser = new ThreadLocalWithUserContext(1);
		ThreadLocalWithUserContext secondUser = new ThreadLocalWithUserContext(2);
		new Thread(firstUser).start();
		new Thread(secondUser).start();

		Thread.sleep(3000);
	}
}