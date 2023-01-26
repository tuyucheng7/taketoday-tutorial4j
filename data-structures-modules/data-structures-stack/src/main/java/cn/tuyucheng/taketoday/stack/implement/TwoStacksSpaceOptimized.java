package cn.tuyucheng.taketoday.stack.implement;

public class TwoStacksSpaceOptimized {
    int[] arr;
    int size;
    int top1, top2;

    public TwoStacksSpaceOptimized(int n) {
        size = n;
        arr = new int[n];
        top1 = -1;
        top2 = size;
    }

    void push1(int x) {
        if (top1 < top2 - 1) {
            top1++;
            arr[top1] = x;
        } else {
            System.out.println("Stack Overflow");
        }
    }

    void push2(int x) {
        if (top2 - 1 > top1) {
            top2--;
            arr[top2] = x;
        } else {
            System.out.println("Stack Overflow");
        }
    }

    int pop1() {
        if (top1 >= 0) {
            int x = arr[top1];
            top1--;
            return x;
        } else {
            System.out.println("Stack Underflow");
        }
        return 0;
    }

    int pop2() {
        if (top2 < size) {
            int x = arr[top2];
            top2++;
            return x;
        } else {
            System.out.println("Stack Underflow");
        }
        return 0;
    }
}