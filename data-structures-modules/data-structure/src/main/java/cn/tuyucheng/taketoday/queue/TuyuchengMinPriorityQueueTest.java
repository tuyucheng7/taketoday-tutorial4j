package cn.tuyucheng.taketoday.queue;

public class TuyuchengMinPriorityQueueTest {
  public static void main(String[] args) {
    TuyuchengMinPriorityQueue<Integer> queue = new TuyuchengMinPriorityQueue<>(15);
    queue.insert(2);
    queue.insert(4);
    queue.insert(5);
    queue.insert(3);
    queue.insert(7);
    queue.insert(1);
    queue.insert(9);
    queue.insert(8);

    for (int i = 0; !queue.isEmpty(); i++) {
      Integer min = queue.deleteMin();
      System.out.print(min + " ");
    }
  }
}