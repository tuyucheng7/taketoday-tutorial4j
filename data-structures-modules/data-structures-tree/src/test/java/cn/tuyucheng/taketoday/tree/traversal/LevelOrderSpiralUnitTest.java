package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LevelOrderSpiralUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderSpiralUsingRecurtive_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderSpiralUsingRecurtive_thenCorrect() {
        LevelOrderSpiral tree = createTree();
        tree.printSpiralUsingRecursive(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderSpiralUsingIterative_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderSpiralUsingIterative_thenCorrect() {
        LevelOrderSpiral tree = createTree();
        tree.printSpiralUsingIterative(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderSpiralUsingDeque_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderSpiralUsingDeque_thenCorrect() {
        LevelOrderSpiral tree = createTree();
        tree.printSpiralUsingDeque(tree.root);
    }

    public LevelOrderSpiral createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(7);
        root.left.right = new Node(6);
        root.right.left = new Node(5);
        root.right.right = new Node(4);
        return new LevelOrderSpiral(root);
    }
}