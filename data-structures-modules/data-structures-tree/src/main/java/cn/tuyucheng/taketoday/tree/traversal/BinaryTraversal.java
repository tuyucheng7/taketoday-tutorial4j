package cn.tuyucheng.taketoday.tree.traversal;

public class BinaryTraversal {
    Node root;

    BinaryTraversal() {
        root = null;
    }

    void preOrder(Node root) {
        if (root == null)
            return;
        System.out.print(root.key + " ");
        preOrder(root.left);
        preOrder(root.right);
    }

    void inOrder(Node root) {
        if (root == null)
            return;
        inOrder(root.left);
        System.out.print(root.key + " ");
        inOrder(root.right);
    }

    void postOrder(Node root) {
        if (root == null)
            return;
        postOrder(root.left);
        postOrder(root.right);
        System.out.print(root.key + " ");
    }
}