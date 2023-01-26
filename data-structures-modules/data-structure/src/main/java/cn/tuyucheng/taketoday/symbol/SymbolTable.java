package cn.tuyucheng.taketoday.symbol;

public class SymbolTable<Key, Value> {
  private Node head;
  private int size;

  public SymbolTable() {
    this.head = new Node(null, null, null);
    this.size = 0;
  }

  public Value get(Key key) {
    Node temp = head;
    while (temp.next != null) {
      temp = temp.next;
      if (temp.key.equals(key)) {
        return temp.value;
      }
    }
    return null;
  }

  public void put(Key key, Value value) {
    Node temp = head;
    while (temp.next != null) {
      temp = temp.next;
      if (key.equals(temp.key)) {
        temp.value = value;
        return;
      }
    }
    Node node = new Node(key, value, null);
    Node oldFirst = head.next;
    node.next = oldFirst;
    head.next = node;
    size++;
  }

  public void delete(Key key) {
    if (isEmpty()) {
      return;
    }
    Node temp = head;
    while (temp.next != null) {
      if (temp.next.key.equals(key)) {
        temp.next = temp.next.next;
        size--;
        return;
      }
      temp = temp.next;
    }
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  private class Node {
    public Key key;
    public Value value;
    public Node next;

    public Node(Key key, Value value, Node next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }
  }
}