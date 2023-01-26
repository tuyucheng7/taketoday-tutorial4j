package cn.tuyucheng.taketoday.linkedlist.doubly;

import cn.tuyucheng.taketoday.linkedlist.single.LinkedList;
import cn.tuyucheng.taketoday.linkedlist.single.Node;

public class SwapKthNode {
    LinkedList linkedList;
    Node head;

    public SwapKthNode(LinkedList linkedList) {
        this.linkedList = linkedList;
        head = linkedList.head;
    }

    public int countNodes() {
        int count = 0;
        Node current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public void swapKth(int k) {
        int n = countNodes();
        if (k > n || 2 * k - 1 == n)
            return;
        Node x = head;
        Node preX = null;
        for (int i = 1; i < k; i++) {
            preX = x;
            x = x.next;
        }
        Node y = head;
        Node preY = null;
        for (int i = 1; i < n - k + 1; i++) {
            preY = y;
            y = y.next;
        }
        if (preX != null)
            preX.next = y;
        if (preY != null)
            preY.next = x;
        Node temp = x.next;
        x.next = y.next;
        y.next = temp;
        if (k == 1)
            head = y;
        if (k == n)
            head = x;
    }
}