package cn.tuyucheng.taketoday.stack.implement;

import java.util.Stack;

public class Queue2 {
    Stack<Integer> stack1 = new Stack<>();
    Stack<Integer> stack2 = new Stack<>();

    public void push(Stack<Integer> stack, int x) {
        stack.push(x);
    }

    public int pop(Stack<Integer> stack) {
        if (stack.isEmpty())
            System.out.println("Stack empty");
        return stack.pop();
    }

    public void enQueue(Queue2 q, int x) {
        push(q.stack1, x);
    }

    public int deQueue(Queue2 q) {
        int x;
        if (q.stack1.isEmpty() && q.stack2.isEmpty())
            System.out.println("Q is Empty");
        if (q.stack2.isEmpty()) {
            while (!q.stack1.isEmpty()) {
                x = pop(stack1);
                push(q.stack2, x);
            }
        }
        x = pop(q.stack2);
        return x;
    }
}