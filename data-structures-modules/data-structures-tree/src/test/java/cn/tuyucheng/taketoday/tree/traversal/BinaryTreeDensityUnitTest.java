package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryTreeDensityUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenComputeDensity_thenShouldSuccess")
    void givenBinaryTree_whenComputeDensity_thenShouldSuccess() {
        BinaryTreeDensity tree = createTree();
        float density = tree.density(tree.root);
        assertEquals(density, 1.5);
    }

    private BinaryTreeDensity createTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        return new BinaryTreeDensity(root);
    }
}