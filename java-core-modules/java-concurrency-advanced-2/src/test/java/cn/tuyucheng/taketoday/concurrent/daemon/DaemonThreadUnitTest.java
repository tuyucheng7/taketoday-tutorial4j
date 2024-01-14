package cn.tuyucheng.taketoday.concurrent.daemon;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DaemonThreadUnitTest {

   @Test
   @Disabled
   public void whenCallIsDaemon_thenCorrect() {
      NewThread daemonThread = new NewThread();
      NewThread userThread = new NewThread();
      daemonThread.setDaemon(true);
      daemonThread.start();
      userThread.start();

      assertTrue(daemonThread.isDaemon());
      assertFalse(userThread.isDaemon());
   }

   @Test(expected = IllegalThreadStateException.class)
   @Disabled
   public void givenUserThread_whenSetDaemonWhileRunning_thenIllegalThreadStateException() {
      NewThread daemonThread = new NewThread();
      daemonThread.start();
      daemonThread.setDaemon(true);
   }
}
