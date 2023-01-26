package cn.tuyucheng.taketoday.queue;

public class TuyuchengMaxPriorityQueueTest {
  public static void main(String[] args) {
    TuyuchengMaxPriorityQueue<Integer> queue = new TuyuchengMaxPriorityQueue<>(20);
    queue.insert(2);
    queue.insert(4);
    queue.insert(5);
    queue.insert(3);
    queue.insert(7);
    queue.insert(1);
    queue.insert(9);
    queue.insert(8);

    while (!queue.isEmpty()) {
      Integer max = queue.deleteMax();
      System.out.print(max + " ");
    }
  }
}