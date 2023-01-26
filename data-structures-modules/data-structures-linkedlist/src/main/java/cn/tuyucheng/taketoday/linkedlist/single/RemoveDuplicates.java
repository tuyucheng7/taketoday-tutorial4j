package cn.tuyucheng.taketoday.linkedlist.single;

import java.util.HashMap;
import java.util.HashSet;

public class RemoveDuplicates {
    LinkedList linkedList;
    Node head;

    public RemoveDuplicates(LinkedList linkedList) {
        this.linkedList = linkedList;
        head = linkedList.head;
    }

    public void removeDuplicates() {
        Node current = head;
        while (current != null) {
            Node temp = current;
            while (temp != null && temp.data == current.data) {
                temp = temp.next;
            }
            current.next = temp;
            current = current.next;
        }
    }

    public void removeDuplicate(Node head) {
        removeDuplicatesUsingRecursive(head);
    }

    public Node removeDuplicatesUsingRecursive(Node head) {
        if (head == null)
            return null;
        if (head.next != null) {
            if (head.data == head.next.data) {
                head.next = head.next.next;
                removeDuplicatesUsingRecursive(head);
            } else
                removeDuplicatesUsingRecursive(head.next);
        }
        return head;
    }

    public void removeDuplicatesUsingOtherMethod() {
        Node current = head;
        Node previous = head;
        while (current != null) {
            if (current.data != previous.data) {
                previous.next = current;
                previous = previous.next;
            }
            current = current.next;
        }
        if (previous != null)
            previous.next = null;
    }

    public void removeDuplicatesUsingMap(Node head) {
        HashMap<Integer, Boolean> map = new HashMap<>();
        Node current = head;
        while (current != null) {
            if (!map.containsKey(current.data))
                map.put(current.data, true);
            current = current.next;
        }
        for (Integer key : map.keySet()) {
            System.out.println(key);
        }
    }

    public void removeDeplicatesInUnSortUsingTwoLoop(Node head) {
        Node current = head;
        Node rest;
        while (current != null && current.next != null) {
            rest = current;
            while (rest.next != null) {
                if (current.data == rest.next.data)
                    rest.next = rest.next.next;
                else
                    rest = rest.next;
            }
            current = current.next;
        }
    }

    public void removeDeplicatesInUnSortUsingHash(Node head) {
        HashSet<Integer> set = new HashSet<>();
        Node current = head;
        Node previous = null;
        while (current != null) {
            int currentData = current.data;
            if (set.contains(current.data))
                previous.next = current.next;
            else {
                set.add(currentData);
                previous = current;
            }
            current = current.next;
        }
    }
}