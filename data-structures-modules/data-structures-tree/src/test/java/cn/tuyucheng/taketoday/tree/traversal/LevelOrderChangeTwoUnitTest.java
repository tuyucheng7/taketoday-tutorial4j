package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LevelOrderChangeTwoUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderEveryTwoLevel_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderEveryTwoLevel_thenCorrect() {
        LevelOrderChangeTwo tree = createTree();
        tree.modifiedLevelOrderUsingStackAndQueue(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintLevelOrderEveryTwoLevelUsingOtherMethod_thenCorrect")
    void givenBinaryTree_whenPrintLevelOrderEveryTwoLevelUsingOtherMethod_thenCorrect() {
        LevelOrderChangeTwo tree = createTree();
        tree.modifiedLevelOrderOtherMethod(tree.root);
    }

    public LevelOrderChangeTwo createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.right.left = new Node(6);
        root.right.right = new Node(7);
        root.left.left.left = new Node(8);
        root.left.left.right = new Node(9);
        root.left.right.left = new Node(3);
        root.left.right.right = new Node(1);
        root.right.left.left = new Node(4);
        root.right.left.right = new Node(2);
        root.right.right.left = new Node(7);
        root.right.right.right = new Node(2);
        root.left.right.left.left = new Node(16);
        root.left.right.left.right = new Node(17);
        root.right.left.right.left = new Node(18);
        root.right.right.left.right = new Node(19);
        return new LevelOrderChangeTwo(root);
    }
}