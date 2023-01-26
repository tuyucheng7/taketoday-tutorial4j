package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContinuousTreeUnitTest {

    @Test
    @DisplayName("givenContinuousTree_whenCheckIfContinuous_thenReturnTrue")
    void givenContinuousTree_whenCheckIfContinuous_thenReturnTrue() {
        ContinuousTree tree = createContinuousTree();
        assertTrue(tree.isContinuous(tree.root));
    }

    @Test
    @DisplayName("givenNotContinuousTree_whenCheckIfContinuous_thenReturnFalse")
    void givenNotContinuousTree_whenCheckIfContinuous_thenReturnFalse() {
        ContinuousTree tree = createNonContinuousTree();
        assertFalse(tree.isContinuous(tree.root));
    }

    @Test
    @DisplayName("givenContinuousTree_whenCheckIfContinuousUsingBFS_thenReturnTrue")
    void givenContinuousTree_whenCheckIfContinuousUsingBFS_thenReturnTrue() {
        ContinuousTree tree = createContinuousTree();
        assertTrue(tree.isContinuous(tree.root));
    }

    @Test
    @DisplayName("givenNotContinuousTree_whenCheckIfContinuousUsingBFS_thenReturnFalse")
    void givenNotContinuousTree_whenCheckIfContinuousUsingBFS_thenReturnFalse() {
        ContinuousTree tree = createNonContinuousTree();
        assertFalse(tree.isContinuous(tree.root));
    }

    private ContinuousTree createContinuousTree() {
        Node root = new Node(3);
        root.left = new Node(2);
        root.right = new Node(4);
        root.left.left = new Node(1);
        root.left.right = new Node(3);
        root.right.right = new Node(5);
        return new ContinuousTree(root);
    }

    private ContinuousTree createNonContinuousTree() {
        Node root = new Node(3);
        root.left = new Node(2);
        root.right = new Node(4);
        root.left.left = new Node(1);
        root.left.right = new Node(3);
        root.right.right = new Node(6);
        return new ContinuousTree(root);
    }
}