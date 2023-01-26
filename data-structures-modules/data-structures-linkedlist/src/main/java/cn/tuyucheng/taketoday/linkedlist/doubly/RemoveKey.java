package cn.tuyucheng.taketoday.linkedlist.doubly;

public class RemoveKey {
    DoublyLinkedList doublyLinkedList;
    Node head;

    public RemoveKey(DoublyLinkedList doublyLinkedList) {
        this.doublyLinkedList = doublyLinkedList;
        head = doublyLinkedList.head;
    }

    public Node deleteAllOccurOfX(Node head, int x) {
        if (head == null)
            return null;
        Node current = head;
        Node next;
        while (current != null) {
            if (current.data == x) {
                next = current.next;
                doublyLinkedList.deleteNode(current);
                current = next;
            } else
                current = current.next;
        }
        return head;
    }
}