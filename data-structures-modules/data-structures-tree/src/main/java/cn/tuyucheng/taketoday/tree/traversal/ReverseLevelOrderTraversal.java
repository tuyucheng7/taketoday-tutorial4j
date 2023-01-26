package cn.tuyucheng.taketoday.tree.traversal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ReverseLevelOrderTraversal {
    Node root;

    public ReverseLevelOrderTraversal(Node root) {
        this.root = root;
    }

    private int height(Node root) {
        if (root == null)
            return 0;
        else {
            int lHeight = height(root.left);
            int rHeight = height(root.right);
            return lHeight > rHeight ? lHeight + 1 : rHeight + 1;
        }
    }

    public void reverseLevelOrder(Node root) {
        int height = height(root);
        int i;
        for (i = height; i >= 1; i--)
            printCurrentLevel(root, i);
    }

    private void printCurrentLevel(Node root, int level) {
        if (level == 0)
            return;
        if (level == 1)
            System.out.print(root.key + " ");
        else {
            if (root.left != null)
                printCurrentLevel(root.left, level - 1);
            if (root.right != null)
                printCurrentLevel(root.right, level - 1);
        }
    }

    public void reverseLevelOrderUsingQueueAndStack(Node root) {
        Stack<Node> S = new Stack<>();
        Queue<Node> Q = new LinkedList<>();
        Q.add(root);
        while (!Q.isEmpty()) {
            Node temp = Q.peek();
            Q.remove();
            S.push(temp);
            if (temp.right != null)
                Q.add(temp.right);
            if (temp.left != null) {
                Q.add(temp.left);
            }
        }
        while (!S.empty()) {
            root = S.peek();
            System.out.print(root.key + " ");
            S.pop();
        }
    }
}