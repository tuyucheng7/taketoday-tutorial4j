package cn.tuyucheng.taketoday.linkedlist.doubly;

public class InsertSortedDoublyLinkedList {
    DoublyLinkedList doublyLinkedList;
    Node head;
    Node tail;

    public InsertSortedDoublyLinkedList(DoublyLinkedList doublyLinkedList) {
        this.doublyLinkedList = doublyLinkedList;
        head = doublyLinkedList.head;
    }

    public static Node sortedInsert(Node head, int newData) {
        Node newNode = new Node(newData);
        newNode.next = newNode.previous = null;
        Node current;
        if (head == null)
            head = newNode;
        else if (head.data > newNode.data) {
            newNode.next = head;
            head.next.previous = newNode;
            head = newNode;
        } else {
            current = head;
            while (current.next != null && current.next.data < newNode.data)
                current = current.next;
            newNode.next = current.next;
            if (current.next != null)
                newNode.next.previous = newNode;
            current.next = newNode;
            newNode.previous = current;
        }
        return head;
    }

    public static void printList(Node head) {
        while (head != null) {
            System.out.print(head.data + " ");
            head = head.next;
        }
    }

    public void nodeInsertTail(int key) {
        Node newNode = new Node(key);
        newNode.next = null;
        if (head == null) {
            newNode.previous = null;
            head = newNode;
            tail = newNode;
            return;
        }
        if (newNode.data < head.data) {
            newNode.previous = null;
            head.previous = newNode;
            newNode.next = head;
            head = newNode;
            return;
        }
        if (newNode.data > tail.data) {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
            return;
        }
        Node temp = head.next;
        while (temp.data < newNode.data)
            temp = temp.next;
        temp.previous.next = newNode;
        newNode.previous = temp.previous;
        temp.previous = newNode;
        newNode.next = temp;
    }
}