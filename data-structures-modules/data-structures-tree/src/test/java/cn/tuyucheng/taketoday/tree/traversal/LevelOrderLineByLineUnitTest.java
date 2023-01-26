package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LevelOrderLineByLineUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderLineByLine_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderLineByLine_thenCorrect() {
        LevelOrderLineByLine tree = createTree();
        tree.printLevelOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderLineByLineUsingQueue_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderLineByLineUsingQueue_thenCorrect() {
        LevelOrderLineByLine tree = createTree();
        tree.printLevelOrderUsingQueue(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderLineByLineUsingTwoQueue_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderLineByLineUsingTwoQueue_thenCorrect() {
        LevelOrderLineByLine tree = createTree();
        tree.printLevelOrderUsingTwoQueue(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderLineByLineUsingOtherMethod_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderLineByLineUsingOtherMethod_thenCorrect() {
        LevelOrderLineByLine tree = createTree();
        tree.printLevelOrderOtherMethod(tree.root);
    }

    private LevelOrderLineByLine createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.left.right.left = new Node(7);
        root.left.right.right = new Node(8);
        root.right.right = new Node(6);
        root.right.right.left = new Node(9);
        return new LevelOrderLineByLine(root);
    }
}