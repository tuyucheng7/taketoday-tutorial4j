package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoldableBinaryTreeUnitTest {

    @Test
    @DisplayName("givenFoldableTree_whenCheckIfFoldable_thenReturnTrue")
    void givenFoldableTree_whenCheckIfFoldable_thenReturnTrue() {
        FoldableBinaryTree tree = createFoldableTree();
        boolean foldable = tree.isFoldable(tree.root);
        assertTrue(foldable);
    }

    @Test
    @DisplayName("givenNotFoldableTree_whenCheckIfFoldable_thenReturnFalse")
    void givenNotFoldableTree_whenCheckIfFoldable_thenReturnFalse() {
        FoldableBinaryTree tree = createNotFoldableTree();
        boolean foldable = tree.isFoldable(tree.root);
        assertFalse(foldable);
    }

    @Test
    @DisplayName("givenFoldableTree_whenCheckIfFoldableUsingOtherMethod_thenReturnTrue")
    void givenFoldableTree_whenCheckIfFoldableUsingOtherMethod_thenReturnTrue() {
        FoldableBinaryTree tree = createFoldableTree();
        boolean foldable = tree.isFoldableOtherMethod(tree.root);
        assertTrue(foldable);
    }

    @Test
    @DisplayName("givenNotFoldableTree_whenCheckIfFoldableUsingOtherMethod_thenReturnFalse")
    void givenNotFoldableTree_whenCheckIfFoldableUsingOtherMethod_thenReturnFalse() {
        FoldableBinaryTree tree = createNotFoldableTree();
        boolean foldable = tree.isFoldableOtherMethod(tree.root);
        assertFalse(foldable);
    }

    @Test
    @DisplayName("givenFoldableTree_whenCheckIfFoldableUsingBFS_thenReturnTrue")
    void givenFoldableTree_whenCheckIfFoldableUsingBFS_thenReturnTrue() {
        FoldableBinaryTree tree = createFoldableTree();
        boolean foldable = tree.isFoldableUsingBFS(tree.root);
        assertTrue(foldable);
    }

    @Test
    @DisplayName("givenNotFoldableTree_whenCheckIfFoldableUsingBFS_thenReturnFalse")
    void givenNotFoldableTree_whenCheckIfFoldableUsingBFS_thenReturnFalse() {
        FoldableBinaryTree tree = createNotFoldableTree();
        boolean foldable = tree.isFoldableUsingBFS(tree.root);
        assertFalse(foldable);
    }

    private FoldableBinaryTree createFoldableTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.right.left = new Node(4);
        root.left.right = new Node(5);
        return new FoldableBinaryTree(root);
    }

    private FoldableBinaryTree createNotFoldableTree() {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.right.left = new Node(4);
        return new FoldableBinaryTree(root);
    }
}