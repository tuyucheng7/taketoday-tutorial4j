package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrintPostOrderUnitTest {

    @Test
    @DisplayName("givenBinaryTreePreInOrder_whenPrintPostOrder_thenCorrect")
    void givenBinaryTreePreInOrder_whenPrintPostOrder_thenCorrect() {
        int[] in = {4, 2, 5, 1, 3, 6};
        int[] pre = {1, 2, 4, 5, 3, 6};
        int n = in.length;
        PrintPostOrder printPostOrder = new PrintPostOrder();
        printPostOrder.printPostOrder(in, pre, n);
    }

    @Test
    @DisplayName("givenBinaryTreePreInOrder_whenPrintPost_thenCorrect")
    void givenBinaryTreePreInOrder_whenPrintPost_thenCorrect() {
        int[] in = {4, 2, 5, 1, 3, 6};
        int[] pre = {1, 2, 4, 5, 3, 6};
        int n = in.length;
        PrintPostOrder printPostOrder = new PrintPostOrder();
        printPostOrder.printPost(in, pre, 0, n - 1);
    }

    @Test
    @DisplayName("givenBinaryTreePreInOrder_whenPrintPostUsingHash_thenCorrect")
    void givenBinaryTreePreInOrder_whenPrintPostUsingHash_thenCorrect() {
        int[] in = {4, 2, 5, 1, 3, 6};
        int[] pre = {1, 2, 4, 5, 3, 6};
        int n = in.length;
        PrintPostOrder printPostOrder = new PrintPostOrder();
        printPostOrder.printPostUsingHashMain(in, pre);
    }

    @Test
    @DisplayName("givenBinaryTreePreOrder_whenPrintPost_thenCorrect")
    void givenBinaryTreePreOrder_whenPrintPost_thenCorrect() {
        int[] pre = {40, 30, 35, 80, 100};
        int n = pre.length;
        PrintPostOrder printPostOrder = new PrintPostOrder();
        printPostOrder.findPostOrderFromPreOrder(pre, n);
    }
}