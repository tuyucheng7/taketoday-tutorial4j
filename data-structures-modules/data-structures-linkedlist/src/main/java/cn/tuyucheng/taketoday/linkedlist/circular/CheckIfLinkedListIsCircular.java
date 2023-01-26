package cn.tuyucheng.taketoday.linkedlist.circular;

public class CheckIfLinkedListIsCircular {

    public static boolean isCircular(Node head) {
        if (head == null)
            return true;
        Node node = head.next;
        while (node != null && node != head)
            node = node.next;
        return node == head;
    }

    public static Node newNode(int data) {
        Node temp = new Node();
        temp.data = data;
        temp.next = null;
        return temp;
    }
}