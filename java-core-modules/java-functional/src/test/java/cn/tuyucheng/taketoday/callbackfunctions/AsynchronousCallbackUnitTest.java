package cn.tuyucheng.taketoday.callbackfunctions;

import cn.tuyucheng.taketoday.callbackfunctions.asynchronous.AsynchronousEventConsumer;
import cn.tuyucheng.taketoday.callbackfunctions.asynchronous.AsynchronousEventListenerImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class AsynchronousCallbackUnitTest {

   @Test
   public void whenCallbackIsInvokedAsynchronously_shouldRunAsynchronousOperation() {
      EventListener listener = Mockito.mock(AsynchronousEventListenerImpl.class);
      AsynchronousEventConsumer asynchronousEventListenerConsumer = new AsynchronousEventConsumer(listener);
      asynchronousEventListenerConsumer.doAsynchronousOperation();

      verify(listener, timeout(1000).times(1)).onTrigger();
   }
}
