package cn.tuyucheng.taketoday.heap;

public class TuyuchengHeapTest {
  public static void main(String[] args) {
    TuyuchengHeap<String> heap = new TuyuchengHeap<>(10);
    heap.insert("A");
    heap.insert("B");
    heap.insert("C");
    heap.insert("D");
    heap.insert("E");
    heap.insert("F");
    heap.insert("G");

    String result;
    while ((result = heap.deleteMax()) != null) {
      System.out.print(result + " ");
    }
  }
}