package cn.tuyucheng.taketoday.heap;

public class TuyuchengHeap<T extends Comparable<T>> {
  private T[] elements;
  private int size;

  public TuyuchengHeap(int capacity) {
    elements = (T[]) new Comparable[capacity + 1];
    this.size = 0;
  }

  private boolean less(int i, int j) {
    return elements[i].compareTo(elements[j]) < 0;
  }

  private void exchange(int i, int j) {
    T temp = elements[i];
    elements[i] = elements[j];
    elements[j] = temp;
  }

  public void insert(T t) {
    elements[++size] = t;
    swim(size);
  }

  private void swim(int size) {
    while (size > 1) {
      if (less(size / 2, size)) {
        exchange(size / 2, size);
      }
      size /= 2;
    }
  }

  public T deleteMax() {
    T max = elements[1];
    exchange(1, size);
    elements[size--] = null;
    sink(1);
    return max;
  }

  private void sink(int i) {
    while (2 * i <= size) {
      int max;
      if (2 * i + 1 <= size) {
        if (less(2 * i, 2 * i + 1)) {
          max = 2 * i + 1;
        } else {
          max = 2 * i;
        }
      } else {
        max = 2 * i;
      }
      if (!less(i, max)) {
        break;
      }
      exchange(i, max);
      i = max;
    }
  }
}