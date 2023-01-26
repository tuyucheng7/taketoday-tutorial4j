package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LevelOrderTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrder_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrder_thenCorrect() {
        LevelOrderTraversal tree = createTree();
        tree.printLevelOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderUsingQueue_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderUsingQueue_thenCorrect() {
        LevelOrderTraversal tree = createTree();
        tree.printLevelOrderUsingQueue(tree.root);
    }

    public LevelOrderTraversal createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        return new LevelOrderTraversal(root);
    }
}