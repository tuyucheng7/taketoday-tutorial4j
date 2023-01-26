package cn.tuyucheng.taketoday.tree.introduction;

import java.util.LinkedList;
import java.util.Queue;

public class FoldableBinaryTree {
    Node root;

    public FoldableBinaryTree(Node root) {
        this.root = root;
    }

    boolean isFoldable(Node root) {
        if (root == null)
            return true;
        mirror(root.left);
        boolean res = isSameStructure(root.left, root.right);
        mirror(root.left);
        return res;
    }

    private boolean isSameStructure(Node a, Node b) {
        if (a == null && b == null)
            return true;
        return a != null && b != null && isSameStructure(a.left, b.left) && isSameStructure(a.right, b.right);
    }

    private void mirror(Node node) {
        if (node == null)
            return;
        else {
            Node temp;
            mirror(node.left);
            mirror(node.right);
            temp = node.left;
            node.left = node.right;
            node.right = temp;
        }
    }

    public boolean isFoldableOtherMethod(Node root) {
        if (root == null)
            return true;
        return isFoldableOtherMethodUtil(root.left, root.right);
    }

    private boolean isFoldableOtherMethodUtil(Node n1, Node n2) {
        if (n1 == null && n2 == null)
            return true;
        if (n1 == null || n2 == null)
            return false;
        return isFoldableOtherMethodUtil(n1.left, n2.right)
                && isFoldableOtherMethodUtil(n1.right, n2.left);
    }

    public boolean isFoldableUsingBFS(Node root) {
        Queue<Node> queue = new LinkedList<>();
        if (root != null) {
            queue.add(root.left);
            queue.add(root.right);
        }
        while (!queue.isEmpty()) {
            Node p = queue.remove();
            Node r = queue.remove();
            if (p == null && r == null)
                continue;
            if ((p == null && r != null) || (p != null && r == null))
                return false;
            else {
                queue.add(p.left);
                queue.add(r.right);
                queue.add(p.right);
                queue.add(r.left);
            }
        }
        return true;
    }
}