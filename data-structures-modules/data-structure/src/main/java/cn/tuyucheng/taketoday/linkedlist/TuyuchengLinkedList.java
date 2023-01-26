package cn.tuyucheng.taketoday.linkedlist;

import java.util.Iterator;

public class TuyuchengLinkedList<T> implements Iterable<T> {
  private Node head;
  private int size;

  public TuyuchengLinkedList() {
    this.head = new Node(null, null);
    this.size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return this.size;
  }

  public void clear() {
    head.next = null;
    size = 0;
  }

  public T get(int i) {
    if (i < 0 || i >= size) {
      throw new IllegalArgumentException("invalid index");
    }
    if (head.next == null) {
      return null;
    }
    Node temp = head.next;
    for (int x = 0; x < i; x++) {
      temp = temp.next;
    }
    return temp.item;
  }

  public void insert(T t) {
    if (isEmpty()) {
      head.next = new Node(t, null);
      size++;
      return;
    }
    Node temp = head;
    for (int x = 0; temp.next != null; x++) {
      temp = temp.next;
    }
    temp.next = new Node(t, null);
    size++;
  }

  public void insert(T t, int i) {
    if (i < 0 || i >= size) {
      throw new IllegalArgumentException("invalid index");
    }
    Node temp = head;
    for (int x = 0; x < i; x++) {
      temp = temp.next;
    }
    temp.next = new Node(t, temp.next);
    size++;
  }

  public T remove(int i) {
    if (i < 0 || i >= size) {
      throw new IllegalArgumentException("invalid index");
    }
    Node temp = head;
    for (int x = 0; x < i; x++) {
      temp = temp.next;
    }
    Node current = temp.next;
    temp.next = current.next;
    size--;
    return current.item;
  }

  public int indexOf(T t) {
    Node temp = head;
    for (int x = 0; x < size; x++) {
      temp = temp.next;
      if (t.equals(temp.item)) {
        return x;
      }
    }
    return -1;
  }

  public void reverse() {
    if (isEmpty()) {
      return;
    }
    reverse(head.next);
  }

  public Node reverse(Node node) {
    if (node.next == null) {
      head.next = node;
      return node;
    }
    Node reverse = reverse(node.next);
    reverse.next = node;
    node.next = null;
    return node;
  }

  @Override
  public Iterator<T> iterator() {
    return new LinkedListIterator();
  }

  private class Node {
    T item;
    Node next;

    public Node(T item, Node next) {
      this.item = item;
      this.next = next;
    }
  }

  private class LinkedListIterator implements Iterator<T> {
    Node temp = head;

    @Override
    public boolean hasNext() {
      return temp.next != null;
    }

    @Override
    public T next() {
      temp = temp.next;
      return temp.item;
    }
  }
}