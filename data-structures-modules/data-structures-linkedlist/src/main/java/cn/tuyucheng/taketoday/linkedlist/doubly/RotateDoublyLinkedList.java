package cn.tuyucheng.taketoday.linkedlist.doubly;

public class RotateDoublyLinkedList {
    DoublyLinkedList doublyLinkedList;
    Node head;

    public RotateDoublyLinkedList(DoublyLinkedList doublyLinkedList) {
        this.doublyLinkedList = doublyLinkedList;
        head = doublyLinkedList.head;
    }

    public void rotate(int n) {
        if (n == 0)
            return;
        Node current = head;
        int count = 1;
        while (current != null && count < n) {
            count++;
            current = current.next;
        }
        if (current == null)
            return;
        Node nthNode = current;
        while (current.next != null)
            current = current.next;
        current.next = head;
        head.previous = current;
        head = nthNode.next;
        head.previous = null;
        nthNode.next = null;
    }
}