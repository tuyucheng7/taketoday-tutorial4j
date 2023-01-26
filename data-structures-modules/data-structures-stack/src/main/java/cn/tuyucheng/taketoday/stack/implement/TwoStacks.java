package cn.tuyucheng.taketoday.stack.implement;

public class TwoStacks {
    int[] arr;
    int size;
    int top1, top2;

    public TwoStacks(int n) {
        arr = new int[n];
        size = n;
        top1 = n / 2 + 1;
        top2 = n / 2;
    }

    void push1(int x) {
        if (top1 > 0) {
            top1--;
            arr[top1] = x;
        } else {
            System.out.print("Stack Overflow" + " By element :" + x + "\n");
        }
    }

    void push2(int x) {
        if (top2 < size - 1) {
            top2++;
            arr[top2] = x;
        } else {
            System.out.print("Stack Overflow" + " By element :" + x + "\n");
        }
    }

    int pop1() {
        if (top1 <= size / 2) {
            int x = arr[top1];
            top1++;
            return x;
        } else {
            System.out.print("Stack UnderFlow");
        }
        return 0;
    }

    int pop2() {
        if (top2 >= size / 2 + 1) {
            int x = arr[top2];
            top2--;
            return x;
        } else {
            System.out.print("Stack UnderFlow");
        }
        return 1;
    }
}