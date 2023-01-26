package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InOrderTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenInOrderTraversalUsingStack_thenCorrect")
    void givenBinaryTree_whenInOrderTraversalUsingStack_thenCorrect() {
        InOrderTraversal tree = createTree();
        tree.inOrderUsingStack(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenUsingMorrisTraversal_thenCorrect")
    void givenBinaryTree_whenUsingMorrisTraversal_thenCorrect() {
        InOrderTraversal tree = createTree();
        tree.morrisTraversal(tree.root);
    }

    public InOrderTraversal createTree() {
        InOrderTraversal tree = new InOrderTraversal();
        tree.root = new Node(1);
        tree.root.left = new Node(2);
        tree.root.right = new Node(3);
        tree.root.left.left = new Node(4);
        tree.root.left.right = new Node(5);
        return tree;
    }
}