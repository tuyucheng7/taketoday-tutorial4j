package cn.tuyucheng.taketoday.concurrent.startathread;

public class SingleThreadExample {
   public static void main(String[] args) {
      NewThread t = new NewThread();
      t.start();
   }
}
