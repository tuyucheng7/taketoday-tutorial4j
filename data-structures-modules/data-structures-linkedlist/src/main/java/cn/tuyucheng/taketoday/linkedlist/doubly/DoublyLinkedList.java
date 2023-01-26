package cn.tuyucheng.taketoday.linkedlist.doubly;

import java.util.Stack;

public class DoublyLinkedList {
    Node head;

    public void printList() {
        Node current = head;
        if (current == null)
            return;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public void push(int newData) {
        Node newNode = new Node(newData);
        newNode.next = head;
        newNode.previous = null;
        if (head != null)
            head.previous = newNode;
        head = newNode;
    }

    public void insertBefore(Node nextNode, int newData) {
        if (nextNode == null)
            return;
        Node newNode = new Node(newData);
        newNode.next = nextNode;
        newNode.previous = nextNode.previous;
        nextNode.previous = newNode;
        if (newNode.previous != null)
            newNode.previous.next = newNode;
        else
            head = newNode;
    }

    public void insertAfter(Node previousNode, int newData) {
        if (previousNode == null)
            return;
        Node newNode = new Node(newData);
        newNode.next = previousNode.next;
        previousNode.next = newNode;
        newNode.previous = previousNode;
        if (newNode.next != null)
            newNode.next.previous = newNode;
    }

    public void append(int newData) {
        Node newNode = new Node(newData);
        newNode.next = null;
        Node last = head;
        if (head == null) {
            newNode.previous = null;
            head = newNode;
            return;
        }
        while (last.next != null)
            last = last.next;
        last.next = newNode;
        newNode.previous = last;
    }

    public void deleteNode(Node deleteNode) {
        if (head == null || deleteNode == null)
            return;
        if (deleteNode == head)
            head = deleteNode.next;
        if (deleteNode.next != null)
            deleteNode.next.previous = deleteNode.previous;
        if (deleteNode.previous != null)
            deleteNode.previous.next = deleteNode.next;
    }

    public void deleteNodeAtGivenPosition(int position) {
        if (head == null || position <= 0)
            return;
        Node current = head;
        int count = 1;
        while (count < position) {
            count++;
            current = current.next;
        }
        if (current == null)
            return;
        deleteNode(current);
    }

    public void reverse() {
        Node temp = null;
        Node current = head;
        while (current != null) {
            temp = current.previous;
            current.previous = current.next;
            current.next = temp;
            current = current.previous;
        }
        if (temp != null)
            head = temp.previous;
    }

    public void reverseWithStack() {
        Stack<Integer> stack = new Stack<>();
        Node temp = head;
        while (temp != null) {
            stack.push(temp.data);
            temp = temp.next;
        }
        temp = head;
        while (!stack.isEmpty()) {
            temp.data = stack.pop();
            temp = temp.next;
        }
    }

    public int findSize(Node head) {
        int size = 0;
        Node temp = head;
        while (temp != null) {
            size++;
            temp = temp.next;
        }
        return size;
    }
}