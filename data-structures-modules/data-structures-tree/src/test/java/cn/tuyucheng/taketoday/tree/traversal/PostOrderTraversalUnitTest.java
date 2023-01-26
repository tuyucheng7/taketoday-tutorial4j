package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostOrderTraversalUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenPostTraversalUsingTwoStack_thenCorrect")
    void givenBinaryTree_whenPostTraversalUsingTwoStack_thenCorrect() {
        PostOrderTraversal tree = createTree();
        tree.postOrderIterativeUsingTwoStack(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPostTraversalUsingOneStack_thenCorrect")
    void givenBinaryTree_whenPostTraversalUsingOneStack_thenCorrect() {
        PostOrderTraversal tree = createTree();
        tree.postOrderIterativeUsingOneStack(tree.root);
    }

    @Test
    @DisplayName("givenBinaryTree_whenPostTraversalUsingHash_thenCorrect")
    void givenBinaryTree_whenPostTraversalUsingHash_thenCorrect() {
        PostOrderTraversal tree = createTree();
        tree.postOrderUsintHash(tree.root);
    }

    private PostOrderTraversal createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.right.left = new Node(6);
        root.right.right = new Node(7);
        return new PostOrderTraversal(root);
    }
}