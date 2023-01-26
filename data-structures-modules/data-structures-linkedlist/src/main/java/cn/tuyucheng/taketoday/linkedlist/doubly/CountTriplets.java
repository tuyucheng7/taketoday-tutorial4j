package cn.tuyucheng.taketoday.linkedlist.doubly;

import java.util.HashMap;

public class CountTriplets {
    DoublyLinkedList doublyLinkedList;
    Node head;

    public CountTriplets(DoublyLinkedList doublyLinkedList) {
        this.doublyLinkedList = doublyLinkedList;
        head = doublyLinkedList.head;
    }

    public static int countTripletsUsingLoop(Node head, int x) {
        int count = 0;
        Node first, second, third;
        for (first = head; first != null; first = first.next)
            for (second = first.next; second != null; second = second.next)
                for (third = second.next; third != null; third = third.next)
                    if ((first.data + second.data + third.data) == x)
                        count++;
        return count;
    }

    public static int countTripletsUsingHash(Node head, int x) {
        Node first, second, third;
        int count = 0;
        HashMap<Integer, Node> valueNodes = new HashMap<>();
        for (first = head; first != null; first = first.next)
            valueNodes.put(first.data, first);
        for (second = head; second != null; second = second.next)
            for (third = second.next; third != null; third = third.next) {
                int p_sum = second.data + third.data;
                if (valueNodes.containsKey(x - p_sum)
                        && valueNodes.get(x - p_sum) != second
                        && valueNodes.get(x - p_sum) != third)
                    count++;
            }
        return count / 3;
    }

    public static int countPairsUsingTwoPointer(Node first, Node second, int value) {
        int count = 0;
        while (first != null && second != null && first != second && second.next != first) {
            if ((first.data + second.data) == value) {
                count++;
                first = first.next;
                second = second.previous;
            } else if ((first.data + second.data) > value)
                second = second.previous;
            else
                first = first.next;
        }
        return count;
    }

    public static int countTripletsUsingTwoPointer(Node head, int x) {
        Node current, first, last;
        int count = 0;
        if (head == null)
            return 0;
        last = head;
        while (last.next != null)
            last = last.next;
        for (current = head; current != null; current = current.next) {
            int value = x - current.data;
            count += countPairsUsingTwoPointer(current.next, last, value);
        }
        return count;
    }
}