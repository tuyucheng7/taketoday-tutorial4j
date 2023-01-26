package cn.tuyucheng.taketoday.symbol;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: java-study
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-16 01:15
 */
@SuppressWarnings("all")
public class OrderSymbolTable<Key extends Comparable<Key>, Value> {
  private Node head;
  private int size;

  public OrderSymbolTable() {
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
    Node curr = head.next;
    Node pre = head;
    while (curr != null && key.compareTo(curr.key) > 0) {
      pre = curr;
      curr = curr.next;
    }
    if (curr != null && key.compareTo(curr.key) == 0) {
      curr.value = value;
      return;
    }
    Node node = new Node(key, value, curr);
    pre.next = node;
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