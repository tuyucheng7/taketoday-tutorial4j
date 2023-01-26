package cn.tuyucheng.taketoday.stack.implement;

public class MergableStack {
    Node head;
    Node tail;

    MergableStack() {
        head = null;
        tail = null;
    }

    void push(int newData) {
        Node newNode = new Node(newData);
        if (head == null) {
            head = newNode;
            head.next = null;
            head.previous = null;
        } else {
            newNode.previous = tail;
            tail.next = newNode;
        }
        tail = newNode;
    }

    void pop() {
        if (head == null)
            System.out.println("Stack Underflow");
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            Node n = tail;
            tail = tail.previous;
            n.previous = null;
            tail.next = null;
        }
    }

    void merge(MergableStack stack) {
        head.previous = stack.tail;
        stack.tail.next = head;
        head = stack.head;
        stack.head = null;
        stack.tail = null;
    }

    void display() {
        if (tail != null) {
            Node n = tail;
            while (n != null) {
                System.out.print(n.data + " ");
                n = n.previous;
            }
            System.out.println();
        } else
            System.out.println("Stack Underflow");
    }

    static class Node {
        Node previous;
        Node next;
        int data;

        public Node(int data) {
            this.data = data;
            previous = null;
            next = null;
        }
    }
}