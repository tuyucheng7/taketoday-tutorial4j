package cn.tuyucheng.taketoday.linkedlist.doubly;

public class FindPairOfSum {
    DoublyLinkedList doublyLinkedList;
    Node head;

    public FindPairOfSum(DoublyLinkedList doublyLinkedList) {
        this.doublyLinkedList = doublyLinkedList;
        head = doublyLinkedList.head;
    }

    public static void pairSum(Node head, int x) {
        Node first = head;
        Node second = head;
        boolean isFound = false;
        while (second.next != null) {
            second = second.next;
        }
        while (first != second && second.next != first) {
            if ((first.data + second.data) == x) {
                isFound = true;
                System.out.println("(" + first.data + ", " + second.data + ")");
                first = first.next;
                second = second.previous;
            } else {
                if ((first.data + second.data) < x)
                    first = first.next;
                else
                    second = second.previous;
            }
        }
        if (isFound == false)
            System.out.println("not found");
    }
}