package cn.tuyucheng.taketoday.concurrent.threadlifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableState implements Runnable {
   public static void main(String[] args) {
      Runnable runnable = new NewState();
      Thread t = new Thread(runnable);
      t.start();
      LOGGER.info("state of thread t: {}", t.getState());
   }

   @Override
   public void run() {
      // not implemented yet ..
   }
}