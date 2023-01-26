package cn.tuyucheng.taketoday.tree.traversal;

import java.util.Arrays;
import java.util.HashMap;

public class PrintPostOrder {
    static int preIndex = 0;

    private int search(int[] arr, int x, int n) {
        for (int i = 0; i < n; i++)
            if (arr[i] == x)
                return i;
        return -1;
    }

    public void printPostOrder(int[] in, int[] pre, int n) {
        int root = search(in, pre[0], n);
        if (root != 0)
            printPostOrder(in, Arrays.copyOfRange(pre, 1, n), root);
        if (root != n - 1)
            printPostOrder(Arrays.copyOfRange(in, root + 1, n), Arrays.copyOfRange(pre, 1 + root, n), n - root - 1);
        System.out.print(pre[0] + " ");
    }

    public void printPost(int[] in, int[] pre, int inStart, int inEnd) {
        if (inStart > inEnd)
            return;
        int inIndex = search(in, inStart, inEnd, pre[preIndex++]);
        printPost(in, pre, inStart, inIndex - 1);
        printPost(in, pre, inIndex + 1, inEnd);
        System.out.print(in[inIndex] + " ");
    }

    private int search(int[] in, int inStart, int inEnd, int data) {
        int i;
        for (i = inStart; i < inEnd; i++)
            if (in[i] == data)
                return i;
        return i;
    }

    public void printPostUsingHashMain(int[] in, int[] pre) {
        int n = pre.length;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            hashMap.put(in[i], i);
        }
        printPostUsingHash(in, pre, 0, n - 1, hashMap);
    }

    private void printPostUsingHash(int[] in, int[] pre, int inStart, int inEnd, HashMap<Integer, Integer> hashMap) {
        if (inStart > inEnd)
            return;
        int inIndex = hashMap.get(pre[preIndex++]);
        printPostUsingHash(in, pre, inStart, inIndex - 1, hashMap);
        printPostUsingHash(in, pre, inIndex + 1, inEnd, hashMap);
        System.out.print(in[inIndex] + " ");
    }

    public void findPostOrderFromPreOrder(int[] pre, int n) {
        INT preIndex = new INT(0);
        findPostOrderFromPreOrderUtil(pre, n, Integer.MIN_VALUE, Integer.MAX_VALUE, preIndex);
    }

    private void findPostOrderFromPreOrderUtil(int[] pre, int n, int minValue, int maxValue, INT preIndex) {
        if (preIndex.data == n)
            return;
        if (pre[preIndex.data] < minValue || pre[preIndex.data] > maxValue)
            return;
        int val = pre[preIndex.data];
        preIndex.data++;
        findPostOrderFromPreOrderUtil(pre, n, minValue, val, preIndex);
        findPostOrderFromPreOrderUtil(pre, n, val, maxValue, preIndex);
        System.out.print(val + " ");
    }

    static class INT {
        int data;

        INT(int data) {
            this.data = data;
        }
    }
}