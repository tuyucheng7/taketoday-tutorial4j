package cn.tuyucheng.taketoday.linkedlist.circular;

public class CircularLinkedList {
    Node head;

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

    public Node addToEmpty(Node last, int newData) {
        if (last != null)
            return last;
        Node newNode = new Node(newData);
        last = newNode;
        newNode.next = last;
        return last;
    }

    public Node addBegin(Node last, int newData) {
        if (last == null)
            return addToEmpty(last, newData);
        Node newNode = new Node(newData);
        newNode.next = last.next;
        last.next = newNode;
        return last;
    }

    public Node addEnd(Node last, int newData) {
        if (last == null)
            return addToEmpty(last, newData);
        Node newNode = new Node(newData);
        newNode.next = last.next;
        last.next = newNode;
        last = newNode;
        return last;
    }

    public Node addAfter(Node last, int newData, int item) {
        if (last == null)
            return null;
        Node p = last.next;
        do {
            if (p.data == item) {
                Node newNode = new Node(newData);
                newNode.next = p.next;
                p.next = newNode;
                if (p == last)
                    last = newNode;
                return last;
            }
            p = p.next;
        } while (p != last.next);
        return last;
    }

    public void printList(Node head) {
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