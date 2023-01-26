package cn.tuyucheng.taketoday.linkedlist.doubly;

import java.util.HashSet;

public class RemoveDuplicate {
    DoublyLinkedList doublyLinkedList;
    Node head;

    public RemoveDuplicate(DoublyLinkedList doublyLinkedList) {
        this.doublyLinkedList = doublyLinkedList;
        head = doublyLinkedList.head;
    }

    public void removeDuplicates(Node head) {
        if (head == null)
            return;
        Node current = head;
        while (current.next != null) {
            if (current.data == current.next.data)
                doublyLinkedList.deleteNode(current.next);
            else
                current = current.next;
        }
    }

    public Node removeDuplicatesInUnsortListUsingLoop(Node head) {
        if (head == null || head.next == null)
            return head;
        Node first, second;
        for (first = head; first != null; first = first.next) {
            second = first.next;
            while (second != null) {
                if (first.data == second.data) {
                    Node next = second.next;
                    doublyLinkedList.deleteNode(second);
                    second = next;
                } else
                    second = second.next;
            }
        }
        return head;
    }

    public Node removeDuplicatesInUnsortListUsingHash(Node head) {
        if (head == null)
            return null;
        HashSet<Integer> nodes = new HashSet<>();
        Node current = head, next;
        while (current != null) {
            if (nodes.contains(current.data)) {
                next = current.next;
                doublyLinkedList.deleteNode(current);
                current = next;
            } else {
                nodes.add(current.data);
                current = current.next;
            }
        }
        return head;
    }
}