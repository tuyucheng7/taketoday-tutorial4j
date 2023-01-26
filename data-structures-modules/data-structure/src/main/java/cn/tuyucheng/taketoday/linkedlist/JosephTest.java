package cn.tuyucheng.taketoday.linkedlist;

public class JosephTest {
  public static void main(String[] args) {
    Node<Integer> first = null;
    Node<Integer> pre = null;
    for (int i = 1; i <= 41; i++) {
      if (i == 1) {
        first = new Node<>(i, null);
        pre = first;
        continue;
      }
      Node<Integer> newNode = new Node<>(i, null);
      pre.next = newNode;
      pre = newNode;

      if (i == 41) {
        pre.next = first;
      }
    }

    int count = 0;
    Node<Integer> node = first;
    Node<Integer> before = null;
    while (node != node.next) {
      count++;
      if (count == 3) {
        before.next = node.next;
        System.out.print(node.item + ",");
        count = 0;
        node = node.next;
      } else {
        before = node;
        node = node.next;
      }
    }
    System.out.println(node.item + ",");
  }

  private static class Node<T> {
    T item;
    Node next;

    public Node(T item, Node next) {
      this.item = item;
      this.next = next;
    }
  }
}