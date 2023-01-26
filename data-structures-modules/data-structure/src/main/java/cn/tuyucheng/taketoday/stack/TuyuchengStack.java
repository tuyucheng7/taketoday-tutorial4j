package cn.tuyucheng.taketoday.stack;

import java.util.Iterator;

public class TuyuchengStack<T> implements Iterable<T> {
  private Node head;
  private int size;

  public TuyuchengStack() {
    head = new Node(null, null);
    size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int getSize() {
    return size;
  }

  public void push(T t) {
    if (isEmpty()) {
      head.next = new Node(t, null);
      size++;
      return;
    }
    Node current = head.next;
    head.next = new Node(t, current);
    size++;
  }

  public T pop() {
    if (isEmpty()) {
      return null;
    }
    Node current = head.next;
    head.next = current.next;
    size--;
    return current.item;
  }

  @Override
  public Iterator<T> iterator() {
    return new TuyuchengStackIterator();
  }

  private class TuyuchengStackIterator implements Iterator {
    Node node;

    public TuyuchengStackIterator() {
      node = head;
    }

    @Override
    public boolean hasNext() {
      return node.next != null;
    }

    @Override
    public Object next() {
      node = node.next;
      return node.item;
    }
  }

  class Node {
    private T item;
    private Node next;

    Node(T item, Node next) {
      this.item = item;
      this.next = next;
    }
  }
}