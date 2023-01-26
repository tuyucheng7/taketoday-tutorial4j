package cn.tuyucheng.taketoday.tree.traversal;

import java.util.LinkedList;
import java.util.Queue;

public class LevelOrderLineByLine {
    Node root;

    public LevelOrderLineByLine(Node root) {
        this.root = root;
    }

    public void printLevelOrder(Node root) {
        int h = height(root);
        for (int i = 1; i <= h; i++) {
            printGivenLevel(root, i);
            System.out.println();
        }
    }

    private void printGivenLevel(Node root, int level) {
        if (level == 0)
            return;
        if (level == 1)
            System.out.print(root.key + " ");
        else if (level > 1) {
            if (root.left != null)
                printGivenLevel(root.left, level - 1);
            if (root.right != null)
                printGivenLevel(root.right, level - 1);
        }
    }

    public int height(Node root) {
        if (root == null)
            return 0;
        else {
            int lHeight = height(root.left);
            int rHeight = height(root.right);
            return lHeight > rHeight ? lHeight + 1 : rHeight + 1;
        }
    }

    public void printLevelOrderUsingQueue(Node root) {
        if (root == null)
            return;
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);
        while (true) {
            int nodesCount = nodes.size();
            if (nodesCount == 0)
                break;
            while (nodesCount > 0) {
                Node temp = nodes.peek();
                nodes.remove();
                System.out.print(temp.key + " ");
                if (temp.left != null)
                    nodes.add(temp.left);
                if (temp.right != null)
                    nodes.add(temp.right);
                nodesCount--;
            }
            System.out.println();
        }
    }

    public void printLevelOrderUsingTwoQueue(Node root) {
        if (root == null)
            return;
        Queue<Node> q1 = new LinkedList<>();
        Queue<Node> q2 = new LinkedList<>();
        q1.add(root);
        while (!q1.isEmpty() || !q2.isEmpty()) {
            while (!q1.isEmpty()) {
                if (q1.peek().left != null)
                    q2.add(q1.peek().left);
                if (q1.peek().right != null)
                    q2.add(q1.peek().right);
                System.out.print(q1.peek().key + " ");
                q1.remove();
            }
            System.out.println();
            while (!q2.isEmpty()) {
                if (q2.peek().left != null)
                    q1.add(q2.peek().left);
                if (q2.peek().right != null)
                    q1.add(q2.peek().right);
                System.out.print(q2.peek().key + " ");
                q2.remove();
            }
            System.out.println();
        }
    }

    public void printLevelOrderOtherMethod(Node root) {
        if (root == null)
            return;
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);
        nodes.add(null);
        while (!nodes.isEmpty()) {
            Node current = nodes.poll();
            if (current != null) {
                if (current.left != null)
                    nodes.add(current.left);
                if (current.right != null)
                    nodes.add(current.right);
                System.out.print(current.key + " ");
            } else {
                if (!nodes.isEmpty()) {
                    nodes.add(null);
                    System.out.println();
                }
            }
        }
    }
}