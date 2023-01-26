package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReverseLevelOrderTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintReverseLevelOrder_thenCourrect")
    void givenBinaryTree_whenPrintReverseLevelOrder_thenCourrect() {
        ReverseLevelOrderTraversal tree = createTree();
        tree.reverseLevelOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintReverseLevelOrderUsingQueueAndStack_thenCourrect")
    void givenBinaryTree_whenPrintReverseLevelOrderUsingQueueAndStack_thenCourrect() {
        ReverseLevelOrderTraversal tree = createTree();
        tree.reverseLevelOrderUsingQueueAndStack(tree.root);
    }

    public ReverseLevelOrderTraversal createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        return new ReverseLevelOrderTraversal(root);
    }
}