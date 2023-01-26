package cn.tuyucheng.taketoday.linkedlist.single;

import java.util.HashSet;

public class InterSection {
    LinkedList linkedList;
    Node head;
    Node a = null;
    Node b = null;
    Node tail = null;
    Node dummy = null;

    public InterSection(LinkedList linkedList) {
        this.linkedList = linkedList;
        head = linkedList.head;
    }

    public InterSection() {
    }

    public void push(int data) {
        Node temp = new Node(data);
        if (dummy == null) {
            dummy = temp;
        } else {
            tail.next = temp;
        }
        tail = temp;
    }

    void printList(Node start) {
        Node p = start;
        while (p != null) {
            System.out.print(p.data + " ");
            p = p.next;
        }
        System.out.println();
    }

    public void sortedIntersectUsingDummyNode() {
        Node p = a, q = b;
        while (p != null && q != null) {
            if (p.data == q.data) {
                push(p.data);
                p = p.next;
                q = q.next;
            } else if (p.data < q.data)
                p = p.next;
            else
                q = q.next;
        }
    }

    public Node sortedIntersectUsingRecursive(Node a, Node b) {
        if (a == null || b == null)
            return null;
        if (a.data < b.data)
            return sortedIntersectUsingRecursive(a.next, b);
        if (a.data > b.data)
            return sortedIntersectUsingRecursive(a, b.next);
        Node result = new Node();
        result.data = a.data;
        result.next = sortedIntersectUsingRecursive(a.next, b.next);
        return result;
    }

    public Integer[] sortedIntersectUsingHash(Node a, Node b, int k) {
        Integer[] result = new Integer[k];
        HashSet<Integer> set = new HashSet<>();
        while (a != null && b != null) {
            if (a.data == b.data) {
                set.add(a.data);
                a = a.next;
                b = b.next;
            } else if (a.data < b.data)
                a = a.next;
            else
                b = b.next;
        }
        return set.toArray(result);
    }
}