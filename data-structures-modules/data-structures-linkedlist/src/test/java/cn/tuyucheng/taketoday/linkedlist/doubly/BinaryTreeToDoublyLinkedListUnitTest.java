package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BinaryTreeToDoublyLinkedListUnitTest {

    @Test
    @DisplayName("givenBinaryTree_whenConvertToDoublyLinkedList_thenSuccess")
    void givenBinaryTree_whenConvertToDoublyLinkedList_thenSuccess() {
        BinaryTreeToDoublyLinkedList tree = createTree();
        TreeNode root = tree.binaryTreeToList(tree.root);
        tree.printList(root);
    }

    public BinaryTreeToDoublyLinkedList createTree() {
        BinaryTreeToDoublyLinkedList tree = new BinaryTreeToDoublyLinkedList();
        tree.root = new TreeNode(10);
        tree.root.left = new TreeNode(12);
        tree.root.right = new TreeNode(15);
        tree.root.left.left = new TreeNode(25);
        tree.root.left.right = new TreeNode(30);
        tree.root.right.left = new TreeNode(36);
        return tree;
    }
}