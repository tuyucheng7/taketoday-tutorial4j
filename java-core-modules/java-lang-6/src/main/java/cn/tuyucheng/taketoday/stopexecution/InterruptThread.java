package cn.tuyucheng.taketoday.stopexecution;

public class InterruptThread extends Thread {
   @Override
   public void run() {
      while (!isInterrupted()) {
         break;
         // business logic
      }
   }
}
