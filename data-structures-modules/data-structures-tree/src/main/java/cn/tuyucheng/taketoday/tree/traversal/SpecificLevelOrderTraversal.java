package cn.tuyucheng.taketoday.tree.traversal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SpecificLevelOrderTraversal {
    Node root;

    public SpecificLevelOrderTraversal(Node root) {
        this.root = root;
    }

    public void printSpecificLevelOrder(Node root) {
        if (root == null)
            return;
        System.out.print(root.key);
        if (root.left != null)
            System.out.print(" " + root.left.key + " " + root.right.key);
        if (root.left.left == null)
            return;
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root.left);
        nodes.add(root.right);
        Node first;
        Node second;
        while (!nodes.isEmpty()) {
            first = nodes.peek();
            nodes.remove();
            second = nodes.peek();
            nodes.remove();
            System.out.print(" " + first.left.key + " " + second.right.key);
            System.out.print(" " + first.right.key + " " + second.left.key);
            if (first.left.left != null) {
                nodes.add(first.left);
                nodes.add(second.right);
                nodes.add(first.right);
                nodes.add(second.left);
            }
        }
    }

    public void printReverseSpecificLevelOrder(Node root) {
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        if (root.left != null) {
            stack.push(root.right);
            stack.push(root.left);
        }
        if (root.left.left != null)
            printReverseSpecificLevelOrderUtil(root, stack);
        while (!stack.isEmpty()) {
            System.out.print(stack.peek().key + " ");
            stack.pop();
        }
    }

    private void printReverseSpecificLevelOrderUtil(Node root, Stack<Node> stack) {
        if (root == null)
            return;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root.left);
        queue.add(root.right);
        Node first;
        Node second;
        while (!queue.isEmpty()) {
            first = queue.peek();
            queue.remove();
            second = queue.peek();
            queue.remove();
            stack.push(second.left);
            stack.push(first.right);
            stack.push(second.right);
            stack.push(first.left);
            if (first.left.left != null) {
                queue.add(first.right);
                queue.add(second.left);
                queue.add(first.left);
                queue.add(second.right);
            }
        }
    }
}