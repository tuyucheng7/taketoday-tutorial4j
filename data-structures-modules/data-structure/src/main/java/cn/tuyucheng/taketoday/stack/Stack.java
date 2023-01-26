package cn.tuyucheng.taketoday.stack;

import java.util.Iterator;

public class Stack<T> implements Iterable<T> {
  private Node head;
  private int size;

  public Stack() {
    this.head = new Node(null, null);
    this.size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return size;
  }

  public void push(T t) {
    if (head.next == null) {
      head.next = new Node(t, null);
      size++;
      return;
    }
    Node first = head.next;
    Node newNode = new Node(t, null);
    head.next = newNode;
    newNode.next = first;
    size++;
  }

  public T pop() {
    Node first = head.next;
    if (first == null) {
      return null;
    }
    head.next = first.next;
    size--;
    return first.item;
  }

  @Override
  public Iterator<T> iterator() {
    return new StackIterator();
  }

  private class Node {
    public T item;
    public Node next;

    public Node(T item, Node next) {
      this.item = item;
      this.next = next;
    }
  }

  private class StackIterator implements Iterator {
    private Node n;

    public StackIterator() {
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