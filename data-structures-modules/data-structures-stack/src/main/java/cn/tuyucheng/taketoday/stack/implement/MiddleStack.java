package cn.tuyucheng.taketoday.stack.implement;

public class MiddleStack {
    Node head;
    Node previous;
    Node next;
    Node mid;
    int size;

    void push(int newData) {
        Node newNode = new Node(newData);
        if (size == 0) {
            newNode.next = null;
            newNode.previous = null;
            head = newNode;
            mid = newNode;
            size++;
            return;
        }
        head.next = newNode;
        newNode.previous = head;
        head = head.next;
        if (size % 2 != 0)
            mid = mid.next;
        size++;
    }

    int pop() {
        int data = -1;
        if (size == 0)
            System.out.println("stack is empty");
        if (size != 0) {
            if (size == 1) {
                head = null;
                mid = null;
            } else {
                data = head.data;
                head = head.previous;
                head.next = null;
                if (size % 2 == 0)
                    mid = mid.previous;
            }
            size--;
        }
        return data;
    }

    int findMiddle() {
        if (size == 0) {
            System.out.println("stack is empty");
            return -1;
        }
        return mid.data;
    }

    void deleteMiddle() {
        if (size != 0) {
            if (size == 1) {
                head = null;
                mid = null;
            } else if (size == 2) {
                head = head.previous;
                mid = mid.previous;
                head.next = null;
            } else {
                mid.next.previous = mid.previous;
                mid.previous.next = mid.next;
                if (size % 2 == 0)
                    mid = mid.previous;
                else
                    mid = mid.next;
            }
            size--;
        }
    }

    static class Node {
        Node previous;
        Node next;
        int data;

        public Node(int data) {
            this.data = data;
        }
    }
}