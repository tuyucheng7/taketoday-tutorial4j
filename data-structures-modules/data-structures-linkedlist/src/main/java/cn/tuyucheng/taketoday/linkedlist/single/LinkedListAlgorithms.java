package cn.tuyucheng.taketoday.linkedlist.single;

import java.util.Stack;

public class LinkedListAlgorithms {
    LinkedList linkedList;
    Node head;
    Node slowPointer, fastPointer, rightPartition;

    public LinkedListAlgorithms(LinkedList linkedList) {
        this.linkedList = linkedList;
        head = linkedList.head;
    }

    public boolean isPalindrome(Node head) {
        Node current = head;
        Stack<Integer> stack = new Stack<>();
        boolean isPalindrome = true;
        while (current != null) {
            stack.push(current.data);
            current = current.next;
        }
        current = head;
        while (current != null) {
            if (!stack.pop().equals(current.data))
                isPalindrome = false;
            current = current.next;
        }
        return isPalindrome;
    }

    public boolean isPalindromeUsingReverse(Node head) {
        boolean isPalindrome = true;
        slowPointer = head;
        fastPointer = head;
        Node previousOfSlowPointer = head;
        Node middleNode = null;
        if (head != null && head.next != null) {
            while (slowPointer != null && fastPointer != null && fastPointer.next != null) {
                fastPointer = fastPointer.next.next;
                previousOfSlowPointer = slowPointer;
                slowPointer = slowPointer.next;
            }
            if (fastPointer != null) {
                middleNode = slowPointer;
                slowPointer = slowPointer.next;
            }
            rightPartition = slowPointer;
            previousOfSlowPointer.next = null;
            reverse();
            isPalindrome = compareLists(head, rightPartition);
            reverse();
            if (middleNode != null) {
                previousOfSlowPointer.next = middleNode;
                middleNode.next = rightPartition;
            } else
                previousOfSlowPointer.next = rightPartition;
        }
        return isPalindrome;
    }

    public void reverse() {
        Node previous = null;
        Node current = rightPartition;
        Node next;
        while (current != null) {
            next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        rightPartition = previous;
    }

    private boolean compareLists(Node list1, Node lise2) {
        Node temp1 = list1;
        Node temp2 = lise2;

        while (temp1 != null && temp2 != null) {
            if (temp1.data != temp2.data)
                return false;
            temp1 = temp1.next;
            temp2 = temp2.next;
        }
        return temp1 == null && temp2 == null;
    }

    public boolean isPalindromeUsingRecursive(Node head) {
        return isPalindromeUtil(head);
    }

    private boolean isPalindromeUtil(Node right) {
        Node left = head;
        if (right == null)
            return true;
        boolean isp = isPalindromeUtil(right.next);
        if (!isp)
            return false;
        boolean isp1 = right.data == left.data;
        left = left.next;
        return isp1;
    }

    public void moveToFront() {
        Node current = head;
        Node previous = null;
        while (current != null && current.next != null) {
            previous = current;
            current = current.next;
        }
        previous.next = null;
        current.next = head;
        head = current;
    }
}