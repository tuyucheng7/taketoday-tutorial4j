package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SpecificLevelOrderTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPrintSpecificLevelOrder_thenCorrect")
    void givenBinaryTree_whenPrintSpecificLevelOrder_thenCorrect() {
        SpecificLevelOrderTraversal tree = createTree();
        tree.printSpecificLevelOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPrintSpecificReverseLevelOrder_thenCorrect")
    void givenBinaryTree_whenPrintSpecificReverseLevelOrder_thenCorrect() {
        SpecificLevelOrderTraversal tree = createTree();
        tree.printReverseSpecificLevelOrder(tree.root);
    }

    private SpecificLevelOrderTraversal createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);

        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.right.left = new Node(6);
        root.right.right = new Node(7);

        root.left.left.left = new Node(8);
        root.left.left.right = new Node(9);
        root.left.right.left = new Node(10);
        root.left.right.right = new Node(11);
        root.right.left.left = new Node(12);
        root.right.left.right = new Node(13);
        root.right.right.left = new Node(14);
        root.right.right.right = new Node(15);

        root.left.left.left.left = new Node(16);
        root.left.left.left.right = new Node(17);
        root.left.left.right.left = new Node(18);
        root.left.left.right.right = new Node(19);
        root.left.right.left.left = new Node(20);
        root.left.right.left.right = new Node(21);
        root.left.right.right.left = new Node(22);
        root.left.right.right.right = new Node(23);
        root.right.left.left.left = new Node(24);
        root.right.left.left.right = new Node(25);
        root.right.left.right.left = new Node(26);
        root.right.left.right.right = new Node(27);
        root.right.right.left.left = new Node(28);
        root.right.right.left.right = new Node(29);
        root.right.right.right.left = new Node(30);
        root.right.right.right.right = new Node(31);
        return new SpecificLevelOrderTraversal(root);
    }
}