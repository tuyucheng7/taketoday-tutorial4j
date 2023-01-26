package cn.tuyucheng.taketoday.linkedlist;

import java.util.Iterator;

public class TuyuchengTwoWayLinkedList<T> implements Iterable<T> {
  private Node head;
  private Node last;
  private int size;

  public TuyuchengTwoWayLinkedList() {
    head = new Node(null, null, null);
    last = null;
    size = 0;
  }

  public void clear() {
    head.next = null;
    last = null;
    size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int length() {
    return size;
  }

  public T getFirst() {
    if (isEmpty()) {
      return null;
    }
    return head.next.item;
  }

  public T getLast() {
    if (isEmpty()) {
      return null;
    }
    return last.item;
  }

  public boolean isOnLeft(int i) {
    return i < size / 2;
  }

  public T get(int i) {
    if (isEmpty()) {
      return null;
    }
    if (isOnLeft(i)) {
      Node temp = head.next;
      for (int x = 0; x < i; x++) {
        temp = temp.next;
      }
      return temp.item;
    } else {
      Node temp = last;
      for (int x = size - 1; size > i; size--) {
        temp = temp.pre;
      }
      return temp.item;
    }
  }

  public void insert(int i, T t) {
    // todo index greater than half reverse order traversal
    if (i < 0 || i >= size) {
      throw new IllegalArgumentException("illegal index");
    }
    Node temp = head;
    for (int x = 0; x < i; x++) {
      temp = temp.next;
    }
    Node current = temp.next;
    Node newNode = new Node(t, temp, current);
    temp.next = newNode;
    current.pre = newNode;
    size++;
  }

  public void insert(T t) {
    if (isEmpty()) {
      last = new Node(t, head, null);
      head.next = last;
    } else {
      Node newNode = new Node(t, last, null);
      last.next = newNode;
      last = newNode;
    }
    size++;
  }

  public T remove(int i) {
    if (isEmpty()) {
      return null;
    }
    if (isOnLeft(i)) {
      Node temp = head;
      for (int x = 0; x < i; x++) {
        temp = temp.next;
      }
      Node current = temp.next;
      if (current.next == null) {
        Node result = temp.next;
        temp.next = null;
        size--;
        return result.item;
      }
      Node nextNode = current.next;
      temp.next = nextNode;
      nextNode.pre = temp;
      size--;
      return current.item;
    } else {
      Node temp = last;
      for (int x = size - 1; x > i; x--) {
        temp = temp.pre;
      }
      Node previous = temp.pre;
      previous.next = temp.next;
      temp.next = previous;
      size--;
      return temp.item;
    }
  }

  public int indexOf(T t) {
    Node temp = head.next;
    for (int i = 0; i < size; i++) {
      if (temp.item.equals(t)) {
        return i;
      }
      temp = temp.next;
    }
    return -1;
  }

  @Override
  public Iterator<T> iterator() {
    return new TwoWayLinkedIterator();
  }

  private class Node {
    T item;
    Node pre;
    Node next;

    public Node(T item, Node pre, Node next) {
      this.item = item;
      this.pre = pre;
      this.next = next;
    }
  }

  private class TwoWayLinkedIterator implements Iterator {
    private Node n;

    public TwoWayLinkedIterator() {
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