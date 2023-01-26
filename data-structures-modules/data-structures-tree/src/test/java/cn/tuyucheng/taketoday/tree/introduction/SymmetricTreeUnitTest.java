package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SymmetricTreeUnitTest {

    @Test
    @DisplayName("givenSymmetricTree_whenCheckIfSymmetric_thenReturnTrue")
    void givenSymmetricTree_whenCheckIfSymmetric_thenReturnTrue() {
        SymmetricTree tree = new SymmetricTree();
        tree.root = new Node(1);
        tree.root.left = new Node(2);
        tree.root.right = new Node(2);
        tree.root.left.left = new Node(3);
        tree.root.left.right = new Node(4);
        tree.root.right.left = new Node(4);
        tree.root.right.right = new Node(3);
        assertTrue(tree.isSymmetirc());
    }

    @Test
    @DisplayName("givenNotSymmetricTree_whenCheckIfSymmetric_thenReturnFalse")
    void givenNotSymmetricTree_whenCheckIfSymmetric_thenReturnFalse() {
        SymmetricTree tree = new SymmetricTree();
        tree.root = new Node(1);
        tree.root.left = new Node(2);
        tree.root.right = new Node(2);
        tree.root.left.left = new Node(3);
        tree.root.left.right = new Node(4);
        tree.root.right.left = new Node(4);
        assertFalse(tree.isSymmetirc());
    }
}