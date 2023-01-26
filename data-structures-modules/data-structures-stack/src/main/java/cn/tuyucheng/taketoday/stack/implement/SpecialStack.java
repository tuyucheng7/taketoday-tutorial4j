package cn.tuyucheng.taketoday.stack.implement;

import java.util.Stack;

public class SpecialStack extends Stack<Integer> {
    Stack<Integer> min = new Stack<>();

    /**
     * 空间优化版本实现
     */
    void push(int x) {
        if (isEmpty()) {
            super.push(x);
            min.push(x);
        } else {
            super.push(x);
            Integer y = min.pop();
            min.push(y);
            if (x <= y)
                min.push(x);
        }
    }

    public Integer pop() {
        Integer x = super.pop();
        Integer y = min.pop();
        if (x != y)
            min.push(y);
        return x;
    }

    // void push(int x) {
    //   if (isEmpty()) {
    //     super.push(x);
    //     min.push(x);
    //   } else {
    //     super.push(x);
    //     Integer y = min.pop();
    //     min.push(y);
    //     if (x < y)
    //       min.push(x);
    //     else
    //       min.push(y);
    //   }
    // }
    //
    // public Integer pop() {
    //   Integer x = super.pop();
    //   min.pop();
    //   return x;
    // }

    int getMin() {
        Integer x = min.pop();
        min.push(x);
        return x;
    }
}