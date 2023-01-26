package cn.tuyucheng.taketoday.linkedlist.single;

public class SegregateEvenOdd {
    LinkedList list;
    Node head;

    public SegregateEvenOdd(LinkedList list) {
        this.list = list;
        head = list.head;
    }

    public void segregateEvenOdd(Node head) {
        Node current = head;
        Node previous = null;
        Node end = head;
        while (end.next != null)
            end = end.next;
        Node newEnd = end;
        while (current.data % 2 != 0 && current != end) {
            newEnd.next = current;
            current = current.next;
            newEnd.next.next = null;
            newEnd = newEnd.next;
        }
        if (current.data % 2 == 0) {
            head = current;
        }
    }
}