package cn.tuyucheng.taketoday.linkedlist.circular;

public class CircularLinkedListDeletion {

    public static Node deleteNode(Node head, int key) {
        if (head == null)
            return null;
        Node current = head;
        Node previous = new Node();
        while (current.data != key) {
            if (current.next == head) {
                System.out.print("\nGiven node is not found" + " in the list!!!");
                break;
            }
            previous = current;
            current = current.next;
        }
        if (current == head && current.next == head) {
            head = null;
            return head;
        }
        if (current == head) {
            previous = head;
            while (previous.next != head)
                previous = previous.next;
            head = current.next;
            previous.next = head;
        } else if (current.next == head)
            previous.next = head;
        else
            previous.next = current.next;
        return head;
    }

    static Node push(Node head, int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        if (head != null) {
            Node temp = head;
            while (temp.next != head)
                temp = temp.next;
            temp.next = newNode;
        } else
            newNode.next = newNode;
        head = newNode;
        return head;
    }

    public static void printList(Node head) {
        Node current = head;
        if (current != null) {
            do {
                int data = current.data;
                System.out.print(data + " ");
                current = current.next;
            } while (current != head);
        }
    }
}