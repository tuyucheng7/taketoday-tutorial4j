package cn.tuyucheng.taketoday.stack.implement;

public class KStack {
    int[] arr;
    int[] top;
    int[] next;
    int n, k;
    int free;

    KStack(int k, int n) {
        this.k = k;
        this.n = n;
        arr = new int[n];
        top = new int[k];
        next = new int[n];
        for (int i = 0; i < k; i++)
            top[i] = -1;
        free = 0;
        for (int i = 0; i < n - 1; i++)
            next[i] = i + 1;
        next[n - 1] = -1;
    }

    boolean isFull() {
        return free == -1;
    }

    boolean isEmpty(int sn) {
        return top[sn] == -1;
    }

    void push(int item, int sn) {
        if (isFull()) {
            System.out.println("Stack Overflow");
            return;
        }
        int i = free;
        free = next[i];
        next[i] = top[sn];
        top[sn] = i;
        arr[i] = item;
    }

    int pop(int sn) {
        if (isEmpty(sn)) {
            System.out.println("Stack Underflow");
            return Integer.MIN_VALUE;
        }
        int i = top[sn];
        top[sn] = next[i];
        next[i] = free;
        free = i;
        return arr[i];
    }
}