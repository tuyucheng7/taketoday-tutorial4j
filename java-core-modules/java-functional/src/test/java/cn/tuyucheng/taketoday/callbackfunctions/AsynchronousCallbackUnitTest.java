package cn.tuyucheng.taketoday.callbackfunctions;

import org.junit.Test;
import org.mockito.Mockito;
import cn.tuyucheng.taketoday.callbackfunctions.EventListener;
import cn.tuyucheng.taketoday.callbackfunctions.asynchronous.AsynchronousEventConsumer;
import cn.tuyucheng.taketoday.callbackfunctions.asynchronous.AsynchronousEventListenerImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.timeout;

public class AsynchronousCallbackUnitTest {

	@Test
	public void whenCallbackIsInvokedAsynchronously_shouldRunAsynchronousOperation() {
		EventListener listener = Mockito.mock(AsynchronousEventListenerImpl.class);
		AsynchronousEventConsumer asynchronousEventListenerConsumer = new AsynchronousEventConsumer(listener);
		asynchronousEventListenerConsumer.doAsynchronousOperation();

		verify(listener, timeout(1000).times(1)).onTrigger();
	}
}
