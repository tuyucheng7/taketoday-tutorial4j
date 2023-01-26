package cn.tuyucheng.taketoday.tree.traversal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class LevelOrderSpiral {
    Node root;

    public LevelOrderSpiral(Node root) {
        this.root = root;
    }

    public void printSpiralUsingRecursive(Node node) {
        int heigth = heigth(node);
        boolean ltr = false;
        for (int i = 1; i <= heigth; i++) {
            printGivenLevel(node, i, ltr);
            ltr = !ltr;
        }
    }

    private void printGivenLevel(Node node, int level, boolean ltr) {
        if (node == null)
            return;
        if (level == 1)
            System.out.print(node.key + " ");
        else if (level > 1) {
            if (!ltr) {
                if (node.left != null)
                    printGivenLevel(node.right, level - 1, ltr);
                if (node.right != null)
                    printGivenLevel(node.left, level - 1, ltr);
            } else {
                if (node.right != null)
                    printGivenLevel(node.left, level - 1, ltr);
                if (node.left != null)
                    printGivenLevel(node.right, level - 1, ltr);
            }
        }
    }

    public int heigth(Node root) {
        if (root == null)
            return 0;
        else {
            int lHeight = heigth(root.left);
            int rHeight = heigth(root.right);
            return lHeight > rHeight ? lHeight + 1 : rHeight + 1;
        }
    }

    public void printSpiralUsingIterative(Node node) {
        if (node == null)
            return;
        Stack<Node> s1 = new Stack<>();
        Stack<Node> s2 = new Stack<>();
        s1.push(node);
        while (!s1.empty() || !s2.empty()) {
            while (!s1.isEmpty()) {
                Node temp = s1.peek();
                s1.pop();
                System.out.print(temp.key + " ");
                if (temp.right != null)
                    s2.push(temp.right);
                if (temp.left != null) {
                    s2.push(temp.left);
                }
            }
            while (!s2.isEmpty()) {
                Node temp = s2.peek();
                s2.pop();
                System.out.print(temp.key + " ");
                if (temp.left != null)
                    s1.push(temp.left);
                if (temp.right != null)
                    s1.push(temp.right);
            }
        }
    }

    public void printSpiralUsingDeque(Node root) {
        Deque<Node> deque = new ArrayDeque<>();
        deque.offer(root);
        boolean reverse = true;
        while (!deque.isEmpty()) {
            int n = deque.size();
            if (!reverse) {
                for (int i = 0; i < n; i++) {
                    if (deque.peekFirst().left != null)
                        deque.offerLast(deque.peekFirst().left);
                    if (deque.peekFirst().right != null)
                        deque.offerLast(deque.peekFirst().right);
                    System.out.print(deque.pollFirst().key + " ");
                }
                reverse = !reverse;
            } else {
                while (n-- > 0) {
                    if (deque.peekLast().right != null)
                        deque.offerFirst(deque.peekLast().right);
                    if (deque.peekLast().left != null)
                        deque.offerFirst(deque.peekLast().left);
                    System.out.print(deque.pollLast().key + " ");
                }
                reverse = !reverse;
            }
        }
    }
}