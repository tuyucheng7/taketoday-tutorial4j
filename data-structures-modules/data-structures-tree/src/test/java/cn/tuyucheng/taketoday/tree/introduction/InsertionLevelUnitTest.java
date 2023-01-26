package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InsertionLevelUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenInsertNodeWithLevelOrder_thenCorrect")
    void givenBinaryTree_whenInsertNodeInOrder_thenCorrect() {
        // given
        Node root = createBT();
        InsertionLevel tree = new InsertionLevel(root);
        tree.inOrder(root);
        System.out.println();

        // when
        tree.insert(root, 12);

        // then
        tree.inOrder(root);
    }

    private Node createBT() {
        Node root;
        root = new Node(10);
        root.left = new Node(11);
        root.left.left = new Node(7);
        root.right = new Node(9);
        root.right.left = new Node(15);
        root.right.right = new Node(8);
        return root;
    }
}