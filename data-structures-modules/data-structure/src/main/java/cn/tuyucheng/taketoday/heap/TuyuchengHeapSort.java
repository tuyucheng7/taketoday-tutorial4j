package cn.tuyucheng.taketoday.heap;

public class TuyuchengHeapSort {

  private static boolean less(Comparable[] heap, int i, int j) {
    return heap[i].compareTo(heap[j]) < 0;
  }

  public static void exchange(Comparable[] heap, int i, int j) {
    Comparable temp = heap[i];
    heap[i] = heap[j];
    heap[j] = temp;
  }

  public static void createHeap(Comparable[] source, Comparable[] heap) {
    System.arraycopy(source, 0, heap, 1, source.length);
    for (int i = heap.length / 2; i > 0; i--) {
      sink(heap, i, heap.length - 1);
    }
  }

  public static void sort(Comparable[] source) {
    Comparable[] heap = new Comparable[source.length + 1];
    createHeap(source, heap);
    int N = heap.length - 1;
    while (N != 1) {
      exchange(heap, 1, N);
      N--;
      sink(heap, 1, N);
    }
    System.arraycopy(heap, 1, source, 0, source.length);
  }

  public static void sink(Comparable[] heap, int target, int range) {
    while (target * 2 <= range) {
      int max;
      if (2 * target + 1 <= range) {
        if (less(heap, target * 2, target * 2 + 1)) {
          max = target * 2 + 1;
        } else {
          max = target * 2;
        }
      } else {
        max = target * 2;
      }
      if (!less(heap, target, max)) {
        break;
      }
      exchange(heap, target, max);
      target = max;
    }
  }
}