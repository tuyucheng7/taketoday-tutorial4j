package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindNthNodeUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenGetNthInorder_thenCorrect")
    void givenBinaryTree_whenGetNthInorder_thenCorrect() {
        FindNthNode tree = createTree();
        int n = 4;
        tree.NthInorder(tree.root, n);
    }

    @Test
    @DisplayName("givenBinaryTree_whenGetNthPostorder_thenCorrect")
    void givenBinaryTree_whenGetNthPostorder_thenCorrect() {
        FindNthNode tree = createTree();
        int n = 4;
        tree.NthPostorder(tree.root, n);
    }

    public FindNthNode createTree() {
        Node root = new Node(10);
        root.left = new Node(20);
        root.right = new Node(30);
        root.left.left = new Node(40);
        root.left.right = new Node(50);
        return new FindNthNode(root);
    }
}