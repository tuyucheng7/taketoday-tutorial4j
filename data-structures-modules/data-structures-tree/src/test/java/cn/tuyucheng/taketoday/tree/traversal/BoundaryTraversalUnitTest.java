package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoundaryTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintBoundaryTraversal_thenCorrect")
    void givenBinaryTree_whenPrintBoundaryTraversal_thenCorrect() {
        BoundaryTraversal tree = createTree();
        tree.printBoundary(tree.root);
    }

    public BoundaryTraversal createTree() {
        Node root = new Node(20);
        root.left = new Node(8);
        root.left.left = new Node(4);
        root.left.right = new Node(12);
        root.left.right.left = new Node(10);
        root.left.right.right = new Node(14);
        root.right = new Node(22);
        root.right.right = new Node(25);
        return new BoundaryTraversal(root);
    }
}