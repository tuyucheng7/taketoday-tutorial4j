package callbackFunctions;

import org.junit.jupiter.api.Test;
import cn.tuyucheng.taketoday.callbackfunctions.EventListener;
import cn.tuyucheng.taketoday.callbackfunctions.synchronous.SynchronousEventConsumer;
import cn.tuyucheng.taketoday.callbackfunctions.synchronous.SynchronousEventListenerImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SynchronousCallbackUnitTest {

	@Test
	public void whenCallbackIsInvokedSynchronously_shouldRunSynchronousOperation() {
		EventListener listener = new SynchronousEventListenerImpl();
		SynchronousEventConsumer synchronousEventConsumer = new SynchronousEventConsumer(listener);
		String result = synchronousEventConsumer.doSynchronousOperation();

		assertNotNull(result);
		assertEquals("Synchronously running callback function", result);
	}
}
