package cn.tuyucheng.taketoday.concurrent.threadsafety;

import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.MessageServiceCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.MessageService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class MessageServiceUnitTest {

	@Test
	void whenCalledgetMessage_thenCorrect() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		MessageService messageService = new MessageService("Welcome to Tuyucheng!");
		Future<String> future1 = executorService.submit(new MessageServiceCallable(messageService));
		Future<String> future2 = executorService.submit(new MessageServiceCallable(messageService));

		assertThat(future1.get()).isEqualTo("Welcome to Tuyucheng!");
		assertThat(future2.get()).isEqualTo("Welcome to Tuyucheng!");
	}
}