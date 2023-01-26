package cn.tuyucheng.taketoday.linkedlist.doubly;

public class BinaryTreeToDoublyLinkedList {
    TreeNode root;

    TreeNode binaryTreeToList(TreeNode root) {
        if (root == null)
            return root;
        root = binaryTreeToListUtil(root);
        while (root.left != null)
            root = root.left;
        return root;
    }

    private TreeNode binaryTreeToListUtil(TreeNode root) {
        if (root == null)
            return root;
        if (root.left != null) {
            TreeNode left = binaryTreeToListUtil(root.left);
            for (; left.right != null; left = left.right) ;
            left.right = root;
            root.left = left;
        }
        if (root.right != null) {
            TreeNode right = binaryTreeToListUtil(root.right);
            for (; right.left != null; right = right.left) ;
            right.left = root;
            root.right = right;
        }
        return root;
    }

    void printList(TreeNode node) {
        while (node != null) {
            System.out.print(node.data + " ");
            node = node.right;
        }
    }
}