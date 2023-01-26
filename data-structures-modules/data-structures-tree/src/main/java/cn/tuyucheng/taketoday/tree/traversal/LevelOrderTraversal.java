package cn.tuyucheng.taketoday.tree.traversal;

import java.util.LinkedList;
import java.util.Queue;

public class LevelOrderTraversal {
    Node root;

    LevelOrderTraversal(Node root) {
        this.root = root;
    }

    public void printLevelOrder(Node root) {
        int h = height(root);
        for (int i = 1; i <= h; i++)
            printCurrentLevel(root, i);
    }

    private void printCurrentLevel(Node root, int level) {
        if (level == 0)
            return;
        if (level == 1) {
            System.out.print(root.key + " ");
        } else if (level > 1) {
            if (root.left != null)
                printCurrentLevel(root.left, level - 1);
            if (root.right != null)
                printCurrentLevel(root.right, level - 1);
        }
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

    public void printLevelOrderUsingQueue(Node root) {
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);
        Node temp;
        while (!nodes.isEmpty()) {
            temp = nodes.peek();
            nodes.remove();
            System.out.print(temp.key + " ");
            if (temp.left != null)
                nodes.add(temp.left);
            if (temp.right != null)
                nodes.add(temp.right);
        }
    }
}