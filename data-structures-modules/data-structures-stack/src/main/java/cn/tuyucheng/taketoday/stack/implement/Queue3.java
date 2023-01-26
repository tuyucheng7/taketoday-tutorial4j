package cn.tuyucheng.taketoday.stack.implement;

import java.util.Stack;

public class Queue3 {
    Stack<Integer> stack = new Stack<>();

    void push(Stack<Integer> stack, int x) {
        stack.push(x);
    }

    int pop(Stack<Integer> stack) {
        if (stack.isEmpty()) {
            System.out.println("stack is empty");
            return -1;
        }
        return stack.pop();
    }

    void enQueue(Queue3 q, int x) {
        push(q.stack, x);
    }

    int deQueue(Queue3 q) {
        int x, res;
        if (stack.isEmpty()) {
            System.out.println("stack is empty");
            return -1;
        } else if (stack.size() == 1)
            return pop(q.stack);
        else {
            x = pop(q.stack);
            res = deQueue(q);
            push(q.stack, x);
            return res;
        }
    }
}