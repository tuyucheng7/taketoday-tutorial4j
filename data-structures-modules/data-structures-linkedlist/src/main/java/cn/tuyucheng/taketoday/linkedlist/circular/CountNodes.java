package cn.tuyucheng.taketoday.linkedlist.circular;

public class CountNodes {

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

    public static int countNodes(Node head) {
        Node temp = head;
        int count = 0;
        if (head != null) {
            do {
                temp = temp.next;
                count++;
            } while (temp != head);
        }
        return count;
    }
}