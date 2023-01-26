package cn.tuyucheng.taketoday.stack.implement;

import java.util.LinkedList;
import java.util.Queue;

public class Stack2 {
    Queue<Integer> q1 = new LinkedList<>();
    Queue<Integer> q2 = new LinkedList<>();
    int currentSize;

    public Stack2() {
        currentSize = 0;
    }

    void remove() {
        if (q1.isEmpty())
            return;
        while (q1.size() != 1) {
            q2.add(q1.peek());
            q1.remove();
        }
        q1.remove();
        currentSize--;
        Queue<Integer> q = q1;
        q1 = q2;
        q2 = q;
    }

    void add(int x) {
        currentSize++;
        q1.add(x);
    }

    int top() {
        if (q1.isEmpty())
            return -1;
        while (q1.size() != 1) {
            q2.add(q1.peek());
            q1.remove();
        }
        Integer temp = q1.peek();
        q1.remove();
        q2.add(temp);
        Queue<Integer> q = q1;
        q1 = q2;
        q2 = q;
        return temp;
    }

    int size() {
        return currentSize;
    }
}