package cn.tuyucheng.taketoday.linkedlist.single;

import java.util.Stack;

public class ReverseList {
    LinkedList list;
    Node head;

    public ReverseList(LinkedList list) {
        this.list = list;
        head = list.head;
    }

    public Node reverseUsingIterative(Node head) {
        Node current = head;
        Node previous = null;
        Node next;
        while (current != null) {
            next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        return previous;
    }

    public Node reverseUsingRecursive(Node head) {
        if (head == null || head.next == null)
            return head;
        Node temp = reverseUsingRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return temp;
    }

    public Node reverseUtilRecursive(Node current, Node previous) {
        if (head == null)
            return head;
        if (current.next == null) {
            head = current;
            current.next = previous;
            return head;
        }
        Node next = current.next;
        current.next = previous;
        reverseUtilRecursive(next, current);
        return head;
    }

    public Node reverseUsingStack(Node head) {
        Node current = head;
        Stack<Node> nodes = new Stack<>();
        while (current.next != null) {
            nodes.push(current);
            current = current.next;
        }
        head = current;
        while (!nodes.isEmpty()) {
            current.next = nodes.peek();
            nodes.pop();
            current = current.next;
        }
        current.next = null;
        return head;
    }
}