package cn.tuyucheng.taketoday.tree.traversal;

import java.util.Stack;

public class InOrderTraversal {
    Node root;

    void inOrderUsingStack(Node root) {
        if (root == null)
            return;
        Stack<Node> stack = new Stack<>();
        Node current = root;
        while (current != null || stack.size() > 0) {
            while (current != null) {
                stack.add(current);
                current = current.left;
            }
            current = stack.pop();
            System.out.print(current.key + " ");
            current = current.right;
        }
    }

    void morrisTraversal(Node root) {
        Node current, previous;
        if (root == null)
            return;
        current = root;
        while (current != null) {
            if (current.left == null) {
                System.out.print(current.key + " ");
                current = current.right;
            } else {
                previous = current.left;
                while (previous.right != null && previous.right != current)
                    previous = previous.right;
                if (previous.right == null) {
                    previous.right = current;
                    current = current.left;
                } else {
                    previous.right = null;
                    System.out.print(current.key + " ");
                    current = current.right;
                }
            }
        }
    }
}