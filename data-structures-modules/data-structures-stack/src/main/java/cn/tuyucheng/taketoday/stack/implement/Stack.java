package cn.tuyucheng.taketoday.stack.implement;

import java.util.LinkedList;
import java.util.Queue;

public class Stack {
    Queue<Integer> q1 = new LinkedList<>();
    Queue<Integer> q2 = new LinkedList<>();
    int currentSize;

    Stack() {
        currentSize = 0;
    }

    void push(int x) {
        currentSize++;
        q2.add(x);
        while (!q1.isEmpty()) {
            q2.add(q1.peek());
            q1.remove();
        }
        Queue<Integer> q = q1;
        q1 = q2;
        q2 = q;
    }

    void pop() {
        if (q1.isEmpty())
            return;
        q1.remove();
        currentSize--;
    }

    int top() {
        if (q1.isEmpty())
            return -1;
        return q1.peek();
    }

    int size() {
        return currentSize;
    }
}