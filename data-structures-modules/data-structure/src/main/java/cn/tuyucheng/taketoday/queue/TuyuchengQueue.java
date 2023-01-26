package cn.tuyucheng.taketoday.queue;

import java.util.Iterator;

public class TuyuchengQueue<T> implements Iterable<T>{
  private Node head;
  private Node last;
  private int size;

  public TuyuchengQueue() {
    this.head = new Node(null, null);
    this.last = null;
    size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int getSize() {
    return size;
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

  public T deQueue() {
    if (isEmpty()) {
      return null;
    }
    Node current = head.next;
    if (current == last) {
      last = null;
      size--;
      return current.item;
    }
    head.next = current.next;
    size--;
    return current.item;
  }

  @Override
  public Iterator<T> iterator() {
    return new QueueIterator();
  }
  
  private class QueueIterator implements Iterator{
    private Node node;

    public QueueIterator() {
      this.node = head;
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

  private class Node {
    private T item;
    private Node next;

    public Node(T item, Node next) {
      this.item = item;
      this.next = next;
    }
  }
}