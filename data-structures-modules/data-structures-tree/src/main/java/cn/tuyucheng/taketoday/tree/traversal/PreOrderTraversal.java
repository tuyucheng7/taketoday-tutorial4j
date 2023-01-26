package cn.tuyucheng.taketoday.tree.traversal;

import java.util.Stack;

public class PreOrderTraversal {
    Node root;

    public PreOrderTraversal(Node root) {
        this.root = root;
    }

    public void preOrder(Node root) {
        if (root == null)
            return;
        System.out.print(root.key + " ");
        preOrder(root.left);
        preOrder(root.right);
    }

    public void morrisTraversalPreorder() {
        morrisTraversalPreorder(root);
    }

    private void morrisTraversalPreorder(Node root) {
        while (root != null) {
            if (root.left == null) {
                System.out.print(root.key + " ");
                root = root.right;
            } else {
                Node current = root.left;
                while (current.right != null && current.right != root)
                    current = current.right;
                if (current.right == root) {
                    current.right = null;
                    root = root.right;
                } else {
                    System.out.print(root.key + " ");
                    current.right = root;
                    root = root.left;
                }
            }
        }
    }

    public void preOrderUsingStack(Node root) {
        if (root == null)
            return;
        Stack<Node> stack = new Stack<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            Node temp = stack.peek();
            System.out.print(temp.key + " ");
            stack.pop();
            if (temp.right != null)
                stack.push(temp.right);
            if (temp.left != null)
                stack.push(temp.left);
        }
    }

    public void preOrderUsingStackOptimization(Node root) {
        if (root == null)
            return;
        Stack<Node> stack = new Stack<>();
        stack.add(root);
        Node current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                System.out.print(current.key + " ");
                if (current.right != null) {
                    stack.push(current.right);
                }
                current = current.left;
            }
            if (!stack.isEmpty())
                current = stack.pop();
        }
    }
}