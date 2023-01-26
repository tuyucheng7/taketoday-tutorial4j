package cn.tuyucheng.taketoday.linkedlist.single;

public class SwapNode {
    LinkedList linkedList;
    Node head;

    public SwapNode(LinkedList linkedList) {
        this.linkedList = linkedList;
        head = linkedList.head;
    }

    public void swapNodes(int x, int y) {
        if (x == y)
            return;
        Node preX = null, curX = head;
        while (curX != null && curX.data != x) {
            preX = curX;
            curX = curX.next;
        }
        Node preY = null, curY = head;
        while (curY != null && curY.data != y) {
            preY = curY;
            curY = curY.next;
        }
        if (curX == null || curY == null)
            return;
        if (preX != null)
            preX.next = curY;
        else
            head = curY;
        if (preY != null)
            preY.next = curX;
        else
            head = curX;
        Node temp = curX.next;
        curX.next = curY.next;
        curY.next = temp;
    }

    public void pairWiseSwapUsingIterative(Node head) {
        Node current = head;
        while (current != null && current.next != null) {
            int k = current.data;
            current.data = current.next.data;
            current.next.data = k;
            current = current.next.next;
        }
    }

    public void pairWiseSwapUsingRecursive(Node head) {
        if (head == null || head.next == null)
            return;
        int k = head.data;
        head.data = head.next.data;
        head.next.data = k;
        pairWiseSwapUsingRecursive(head.next.next);
    }
}