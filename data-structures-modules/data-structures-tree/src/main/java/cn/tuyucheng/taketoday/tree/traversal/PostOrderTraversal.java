package cn.tuyucheng.taketoday.tree.traversal;

import java.util.HashSet;
import java.util.Stack;

public class PostOrderTraversal {
    Node root;

    public PostOrderTraversal(Node root) {
        this.root = root;
    }

    public void postOrderIterativeUsingTwoStack(Node root) {
        if (root == null)
            return;
        Stack<Node> stack1 = new Stack<>();
        Stack<Node> stack2 = new Stack<>();
        stack1.push(root);
        while (!stack1.isEmpty()) {
            Node temp = stack1.pop();
            stack2.push(temp);
            if (temp.left != null)
                stack1.push(temp.left);
            if (temp.right != null)
                stack1.push(temp.right);
        }
        while (!stack2.isEmpty()) {
            Node temp = stack2.pop();
            System.out.print(temp.key + " ");
        }
    }

    public void postOrderIterativeUsingOneStack(Node root) {
        if (root == null)
            return;
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        Node previous = null;
        while (!stack.isEmpty()) {
            Node current = stack.peek();
            if (previous == null || previous.left == current || previous.right == current) {
                if (current.left != null)
                    stack.push(current.left);
                else if (current.right != null) {
                    stack.push(current.right);
                } else {
                    stack.pop();
                    System.out.print(current.key + " ");
                }
            } else if (current.left == previous) {
                if (current.right != null)
                    stack.push(current.right);
                else {
                    stack.pop();
                    System.out.print(current.key + " ");
                }
            } else if (current.right == previous) {
                stack.pop();
                System.out.print(current.key + " ");
            }
            previous = current;
        }
    }

    public void postOrderIterativeUsingOneStackOptimization(Node root) {
        Stack<Node> stack = new Stack<>();
        while (true) {
            while (root != null) {
                stack.push(root);
                stack.push(root);
                root = root.left;
            }
            if (stack.empty())
                return;
            root = stack.pop();
            if (!stack.empty() && stack.peek() == root)
                root = root.right;
            else {
                System.out.print(root.key + " ");
                root = null;
            }
        }
    }

    public void postOrderUsintHash(Node root) {
        Node temp = root;
        HashSet<Node> visited = new HashSet<>();
        while (temp != null && !visited.contains(temp)) {
            if (temp.left != null && !visited.contains(temp.left))
                temp = temp.left;
            else if (temp.right != null && !visited.contains(temp.right))
                temp = temp.right;
            else {
                System.out.print(temp.key + " ");
                visited.add(temp);
                temp = root;
            }
        }
    }
}