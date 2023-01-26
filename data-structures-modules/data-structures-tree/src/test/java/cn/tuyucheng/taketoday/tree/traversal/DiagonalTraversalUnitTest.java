package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiagonalTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenDiagonalTraversalUsingMap_thenCorrect")
    void givenBinaryTree_whenDiagonalTraversalUsingMap_thenCorrect() {
        DiagonalTraversal tree = createTree();
        tree.diagonalPrintUsingMap(tree.root);
    }

    private DiagonalTraversal createTree() {
        Node root = new Node(8);
        root.left = new Node(3);
        root.right = new Node(10);
        root.left.left = new Node(1);
        root.left.right = new Node(6);
        root.right.right = new Node(14);
        root.right.right.left = new Node(13);
        root.left.right.left = new Node(4);
        root.left.right.right = new Node(7);
        return new DiagonalTraversal(root);
    }
}