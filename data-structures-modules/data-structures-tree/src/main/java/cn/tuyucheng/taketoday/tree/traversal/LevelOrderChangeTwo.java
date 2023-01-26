package cn.tuyucheng.taketoday.tree.traversal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class LevelOrderChangeTwo {
    static final int LEFT = 0;
    static final int RIGHT = 1;
    Node root;

    public LevelOrderChangeTwo(Node root) {
        this.root = root;
    }

    private static int changeDirection(int Dir) {
        Dir = 1 - Dir;
        return Dir;
    }

    public void modifiedLevelOrderUsingStackAndQueue(Node root) {
        if (root == null)
            return;
        if (root.left == null && root.right == null) {
            System.out.print(root.key + " ");
            return;
        }
        Stack<Node> stack = new Stack<>();
        Queue<Node> queue = new LinkedList<>();
        Node temp = null;
        int count = 0;
        int nodeCounts;
        queue.add(root);
        boolean rightToLeft = false;
        while (!queue.isEmpty()) {
            count++;
            nodeCounts = queue.size();
            for (int i = 0; i < nodeCounts; i++) {
                temp = queue.peek();
                queue.remove();
                if (!rightToLeft)
                    System.out.print(temp.key + " ");
                else
                    stack.push(temp);
                if (temp.left != null)
                    queue.add(temp.left);
                if (temp.right != null)
                    queue.add(temp.right);
            }
            if (rightToLeft) {
                while (!stack.isEmpty()) {
                    temp = stack.peek();
                    stack.pop();
                    System.out.print(temp.key + " ");
                }
            }
            if (count == 2) {
                rightToLeft = !rightToLeft;
                count = 0;
            }
            System.out.println();
        }
    }

    public void modifiedLevelOrderOtherMethod(Node root) {
        if (root == null)
            return;
        int dir = LEFT;
        Node temp;
        Queue<Node> Q = new LinkedList<>();
        Stack<Node> S = new Stack<>();
        S.add(root);
        while (!Q.isEmpty() || !S.isEmpty()) {
            while (!S.isEmpty()) {
                temp = S.peek();
                S.pop();
                System.out.print(temp.key + " ");
                if (dir == LEFT) {
                    if (temp.left != null)
                        Q.add(temp.left);
                    if (temp.right != null)
                        Q.add(temp.right);
                } else {
                    if (temp.right != null)
                        Q.add(temp.right);
                    if (temp.left != null)
                        Q.add(temp.left);
                }
            }
            System.out.println();
            while (!Q.isEmpty()) {
                temp = Q.peek();
                Q.remove();
                System.out.print(temp.key + " ");
                if (dir == LEFT) {
                    if (temp.left != null)
                        S.add(temp.left);
                    if (temp.right != null)
                        S.add(temp.right);
                } else {
                    if (temp.right != null)
                        S.add(temp.right);
                    if (temp.left != null)
                        S.add(temp.left);
                }
            }
            System.out.println();
            dir = changeDirection(dir);
        }
    }
}