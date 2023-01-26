package cn.tuyucheng.taketoday.list;

import java.util.Iterator;

public class TuyuchengSequenceList<T> implements Iterable<T> {
  private T[] array;
  private int size;

  public TuyuchengSequenceList(int capacity) {
    this.array = (T[]) new Object[capacity];
    size = 0;
  }

  public void clear() {
    size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int length() {
    return size;
  }

  public T get(int i) {
    if (i < 0 || i >= size) {
      throw new IllegalArgumentException("index invalid");
    }
    return array[i];
  }

  public void insert(T t) {
    if (size == array.length) {
      throw new RuntimeException("list is full");
    }
    array[size++] = t;
  }

  public void insert(T t, int i) {
    if (i == array.length) {
      throw new RuntimeException("list is full");
    }
    if (i < 0 || i >= size) {
      throw new IllegalArgumentException("index invalid");
    }
    for (int x = size - 1; x >= i; x--) {
      array[x + 1] = array[x];
    }
    array[i] = t;
    size++;
  }

  public T remove(int i) {
    if (i < 0 || i > size - 1) {
      throw new IllegalArgumentException("index invalid");
    }
    T result = array[i];
    for (int x = i; x < size - 1; x++) {
      array[x] = array[x + 1];
    }
    size--;
    return result;
  }

  public int indexOf(T t) {
    for (int i = 0; i < size; i++) {
      if (t.equals(array[i])) {
        return i;
      }
    }
    return -1;
  }

  public void reSize(int newSize) {
    T[] temp = array;
    array = (T[]) new Object[newSize];
    for (int x = 0; x < size; x++) {
      array[x] = temp[x];
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new ListIterator();
  }

  private class ListIterator implements Iterator<T> {
    private int cusor;

    public ListIterator() {
      this.cusor = 0;
    }

    @Override
    public boolean hasNext() {
      return cusor < size;
    }

    @Override
    public T next() {
      return array[cusor++];
    }
  }
}