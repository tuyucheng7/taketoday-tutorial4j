package cn.tuyucheng.taketoday.concurrent.threadfactory;

public class Demo {

   public void execute() {
      TuyuchengThreadFactory factory = new TuyuchengThreadFactory("TuyuchengThreadFactory");
      for (int i = 0; i < 10; i++) {
         Thread t = factory.newThread(new Task());
         t.start();
      }
   }
}