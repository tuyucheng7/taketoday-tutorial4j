package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PreOrderTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPreOrderUsingMorris_thenCorrect")
    void givenBinaryTree_whenPreOrderUsingMorris_thenCorrect() {
        PreOrderTraversal tree = createTree();
        tree.morrisTraversalPreorder();
        System.out.println();
        tree.preOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPreOrderUsingStack_thenCorrect")
    void givenBinaryTree_whenPreOrderUsingStack_thenCorrect() {
        PreOrderTraversal tree = createTree();
        tree.preOrderUsingStack(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPreOrderUsingStackOptimization_thenCorrect")
    void givenBinaryTree_whenPreOrderUsingStackOptimization_thenCorrect() {
        PreOrderTraversal tree = createTree();
        tree.preOrderUsingStackOptimization(tree.root);
    }

    private PreOrderTraversal createTree() {
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
        return new PreOrderTraversal(root);
    }
}