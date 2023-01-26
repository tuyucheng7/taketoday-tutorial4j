package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BinaryTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenUsingPreOrderTraversal_thenCorrect")
    void givenBinaryTree_whenUsingPreOrderTraversal_thenCorrect() {
        BinaryTraversal tree = createTree();
        tree.preOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenUsingInOrderTraversal_thenCorrect")
    void givenBinaryTree_whenUsingInOrderTraversal_thenCorrect() {
        BinaryTraversal tree = createTree();
        tree.inOrder(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenUsingPostOrderTraversal_thenCorrect")
    void givenBinaryTree_whenUsingPostOrderTraversal_thenCorrect() {
        BinaryTraversal tree = createTree();
        tree.postOrder(tree.root);
    }

    public BinaryTraversal createTree() {
        BinaryTraversal tree = new BinaryTraversal();
        tree.root = new Node(1);
        tree.root.left = new Node(2);
        tree.root.right = new Node(3);
        tree.root.left.left = new Node(4);
        tree.root.left.right = new Node(5);
        return tree;
    }
}