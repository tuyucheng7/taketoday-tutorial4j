package cn.tuyucheng.taketoday.linkedlist.circular;

public class ConvertSinglyLinkedList {

    public static Node convert(Node head) {
        Node start = head;
        while (head.next != null)
            head = head.next;
        head.next = start;
        return start;
    }

    public static void displayList(Node node) {
        Node start = node;
        while (node.next != start) {
            System.out.print(" " + node.data);
            node = node.next;
        }
        System.out.print(" " + node.data);
    }

    static Node push(Node head, int data) {
        Node newNode = new Node();
        newNode.data = data;
        newNode.next = (head);
        (head) = newNode;
        return head;
    }
}