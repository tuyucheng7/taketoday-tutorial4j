package cn.tuyucheng.taketoday.queue;

import java.util.Iterator;

public class Queue<T> implements Iterable<T> {
  private Node head;
  private Node last;
  private int size;

  public Queue() {
    this.head = new Node(null, null);
    this.last = null;
    this.size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return size;
  }

  public T deQueue() {
    if (isEmpty()) {
      return null;
    }
    Node pre = head.next;
    if (pre == last) {
      last = null;
      size--;
      return pre.item;
    }
    head.next = pre.next;
    size--;
    return pre.item;
  }

  public void enQueue(T t) {
    if (last == null) {
      last = new Node(t, null);
      head.next = last;
    } else {
      Node oldLast = last;
      last = new Node(t, null);
      oldLast.next = last;
    }
    size++;
  }

  @Override
  public Iterator iterator() {
    return new QueueIterator();
  }

  private class Node {
    public T item;
    public Node next;

    public Node(T item, Node next) {
      this.item = item;
      this.next = next;
    }
  }

  private class QueueIterator implements Iterator {
    private Node n;

    public QueueIterator() {
      this.n = head;
    }

    @Override
    public boolean hasNext() {
      return n.next != null;
    }

    @Override
    public Object next() {
      n = n.next;
      return n.item;
    }
  }
}