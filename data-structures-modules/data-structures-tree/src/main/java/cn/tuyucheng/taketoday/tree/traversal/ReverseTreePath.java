package cn.tuyucheng.taketoday.tree.traversal;

import java.util.HashMap;
import java.util.Map;

public class ReverseTreePath {
    Node root;

    public ReverseTreePath(Node root) {
        this.root = root;
    }

    public void reverseTreePath(Node root, int data) {
        Map<Integer, Integer> temp = new HashMap<>();
        INT nextPos = new INT();
        nextPos.data = 0;
        reverseTreePathUtil(root, data, temp, 0, nextPos);
    }

    private Node reverseTreePathUtil(Node root, int data, Map<Integer, Integer> temp, int level, INT nextPos) {
        if (root == null)
            return null;
        if (root.key == data) {
            temp.put(level, root.key);
            root.key = temp.get(nextPos.data);
            nextPos.data++;
            return root;
        }
        temp.put(level, root.key);
        Node left, right = null;
        left = reverseTreePathUtil(root.left, data, temp, level + 1, nextPos);
        if (left == null)
            right = reverseTreePathUtil(root.right, data, temp, level + 1, nextPos);
        if (left != null || right != null) {
            root.key = temp.get(nextPos.data);
            nextPos.data++;
            return left != null ? left : right;
        }
        return null;
    }

    public void printInOrder(Node root) {
        if (root != null) {
            printInOrder(root.left);
            System.out.print(root.key + " ");
            printInOrder(root.right);
        }
    }

    static class INT {
        int data;
    }
}