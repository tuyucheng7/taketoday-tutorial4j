package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BinaryTreeUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenAddNode_thenSuccess")
    void givenBinaryTree_whenAddNode_thenSuccess() {
        BinaryTree tree = new BinaryTree();
        tree.root = new Node(1);
        tree.root.left = new Node(2);
        tree.root.right = new Node(3);
        tree.root.left.left = new Node(4);
    }
}