package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReverseTreePathUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenReverseTreePath_thenCorrect")
    void givenBinaryTree_whenReverseTreePath_thenCorrect() {
        ReverseTreePath tree = createTree();
        int data = 4;
        tree.reverseTreePath(tree.root, data);
        tree.printInOrder(tree.root);
    }

    private ReverseTreePath createTree() {
        Node root = new Node(7);
        root.left = new Node(6);
        root.right = new Node(5);
        root.left.left = new Node(4);
        root.left.right = new Node(3);
        root.right.left = new Node(2);
        root.right.right = new Node(1);
        return new ReverseTreePath(root);
    }
}