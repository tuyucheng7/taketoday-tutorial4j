package cn.tuyucheng.taketoday.tree.construction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConstructTreeFromPreInOrderUnitTest {

    @Test
    @DisplayName("givenPreAndInOrderSequence_whenConstructTree_thenShouldSuccess")
    void givenPreAndInOrderSequence_whenConstructTree_thenShouldSuccess() {
        char[] in = new char[]{'D', 'B', 'E', 'A', 'F', 'C'};
        char[] pre = new char[]{'A', 'B', 'D', 'E', 'C', 'F'};
        ConstructTreeFromPreInOrder builder = new ConstructTreeFromPreInOrder();
        int n = in.length;
        ConstructTreeFromPreInOrder.Node tree = builder.buildTree(in, pre, 0, n - 1);
        builder.printInorder(tree);
    }

    @Test
    @DisplayName("givenPreAndInOrderSequence_whenConstructTreeUsingHash_thenShouldSuccess")
    void givenPreAndInOrderSequence_whenConstructTreeUsingHash_thenShouldSuccess() {
        char[] in = new char[]{'D', 'B', 'E', 'A', 'F', 'C'};
        char[] pre = new char[]{'A', 'B', 'D', 'E', 'C', 'F'};
        ConstructTreeFromPreInOrder builder = new ConstructTreeFromPreInOrder();
        int n = in.length;
        ConstructTreeFromPreInOrder.Node tree = builder.buildTreeUsingHashWrap(in, pre, n);
        builder.printInorder(tree);
    }
}