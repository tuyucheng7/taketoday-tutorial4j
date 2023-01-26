package cn.tuyucheng.taketoday.tree.introduction;

import java.util.LinkedList;
import java.util.Queue;

public class ContinuousTree {
    Node root;

    public ContinuousTree(Node root) {
        this.root = root;
    }

    public boolean isContinuous(Node root) {
        if (root == null)
            return true;
        if (root.left == null && root.right == null)
            return true;
        if (root.left == null)
            return (Math.abs(root.key - root.right.key) == 1) && isContinuous(root.right);
        if (root.right == null)
            return (Math.abs(root.key - root.left.key) == 1) && isContinuous(root.left);
        return (Math.abs(root.key - root.left.key) == 1)
                && (Math.abs(root.key - root.right.key) == 1)
                && isContinuous(root.left)
                && isContinuous(root.right);
    }

    public boolean isContinuousBFS(Node root) {
        if (root == null)
            return true;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        Node temp;
        while (!queue.isEmpty()) {
            temp = queue.peek();
            queue.remove();
            if (temp.left != null) {
                if (Math.abs(temp.key - temp.left.key) != 1)
                    return false;
                queue.add(temp.left);
            }
            if (temp.right != null) {
                if (Math.abs(temp.key - temp.right.key) != 1)
                    return false;
                queue.add(temp.right);
            }
        }
        return true;
    }
}