package cn.tuyucheng.taketoday.queue;

public class TuyuchengQueueTest {
  public static void main(String[] args) {
    TuyuchengQueue<Integer> queue = new TuyuchengQueue<>();
    queue.enQueue(1);
    queue.enQueue(2);
    queue.enQueue(3);
    for (Integer integer : queue) {
      System.out.println(integer);
    }

    System.out.println(queue.deQueue());
    System.out.println(queue.deQueue());
    System.out.println(queue.getSize());
    System.out.println(queue.deQueue());
    System.out.println(queue.deQueue());
  }
}