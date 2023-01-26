package cn.tuyucheng.taketoday.tree.traversal;

public class BinaryTreeDensity {
    Node root;

    public BinaryTreeDensity(Node root) {
        this.root = root;
    }

    public float density(Node root) {
        Size size = new Size();
        if (root == null)
            return 0;
        int height = heightAndSize(root, size);
        return (float) size.size / height;
    }

    private int heightAndSize(Node node, Size size) {
        if (node == null)
            return 0;
        int l = heightAndSize(node.left, size);
        int r = heightAndSize(node.right, size);
        size.size++;
        return l > r ? l + 1 : r + 1;
    }

    static class Size {
        int size = 0;
    }
}