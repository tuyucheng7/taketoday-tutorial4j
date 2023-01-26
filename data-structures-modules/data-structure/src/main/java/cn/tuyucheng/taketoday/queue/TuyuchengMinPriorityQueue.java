package cn.tuyucheng.taketoday.queue;

public class TuyuchengMinPriorityQueue<T extends Comparable<T>> {
  private T[] elements;
  private int size;

  public TuyuchengMinPriorityQueue(int capacity) {
    elements = (T[]) new Comparable[capacity + 1];
    size = 0;
  }

  public int getSize() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
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

  private void swim(int index) {
    while (index > 1) {
      if (less(index, index / 2)) {
        exchange(index, index / 2);
      }
      index /= 2;
    }
  }

  public T deleteMin() {
    T min = elements[1];
    exchange(1, size);
    size--;
    sink(1);
    return min;
  }

  private void sink(int index) {
    while (2 * index <= size) {
      int min;
      if (2 * index + 1 <= size) {
        if (less(index * 2, index * 2 + 1)) {
          min = index * 2;
        } else {
          min = index * 2 + 1;
        }
      } else {
        min = index * 2;
      }
      if (less(index, min)) {
        break;
      }
      exchange(index, min);
      index = min;
    }
  }
}