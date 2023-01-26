package cn.tuyucheng.taketoday.stack.implement;

import java.util.Stack;

public class Queue {
    Stack<Integer> stack1 = new Stack<>();
    Stack<Integer> stack2 = new Stack<>();

    void enQueue(int x) {
        while (!stack1.isEmpty())
            stack2.push(stack1.pop());
        stack1.push(x);
        while (!stack2.isEmpty())
            stack1.push(stack2.pop());
    }

    int deQueue() {
        if (stack1.isEmpty()) {
            System.out.println("Queue is empty");
            return -1;
        }
        Integer x = stack1.peek();
        stack1.pop();
        return x;
    }
}