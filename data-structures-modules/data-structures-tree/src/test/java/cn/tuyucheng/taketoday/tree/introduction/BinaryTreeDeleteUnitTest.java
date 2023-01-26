package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BinaryTreeDeleteUnitTest {

    @Test
    @DisplayName("givenBT_whenDeleteNode_thenCorrect")
    void givenBT_whenDeleteNode_thenCorrect() {
        BinaryTreeDelete tree = createBT();
        tree.inOrder(tree.root);
        tree.deleteNode(tree.root, 11);
        System.out.println();
        tree.inOrder(tree.root);
    }

    public BinaryTreeDelete createBT() {
        Node root = new Node(10);
        root.left = new Node(11);
        root.left.left = new Node(7);
        root.left.right = new Node(12);
        root.right = new Node(9);
        root.right.left = new Node(15);
        root.right.right = new Node(8);
        return new BinaryTreeDelete(root);
    }
}