package cn.tuyucheng.taketoday.tree.traversal;

public class BoundaryTraversal {
    Node root;

    public BoundaryTraversal(Node root) {
        this.root = root;
    }

    public void printBoundary(Node root) {
        if (root == null)
            return;
        System.out.print(root.key + " ");
        printBoundaryLeft(root.left);
        printLeaves(root.left);
        printLeaves(root.right);
        printBoundaryRight(root.right);
    }

    private void printBoundaryLeft(Node node) {
        if (node == null)
            return;
        if (node.left != null) {
            System.out.print(node.key + " ");
            printBoundaryLeft(node.left);
        } else if (node.right != null) {
            System.out.print(node.key + " ");
            printBoundaryLeft(node.right);
        }
    }

    private void printBoundaryRight(Node node) {
        if (node == null)
            return;
        if (node.right != null) {
            printBoundaryRight(node.right);
            System.out.print(node.key + " ");
        } else if (node.left != null) {
            printBoundaryRight(node.left);
            System.out.print(node.key + " ");
        }
    }

    private void printLeaves(Node node) {
        if (node == null)
            return;
        printLeaves(node.left);
        if (node.left == null && node.right == null)
            System.out.print(node.key + " ");
        printLeaves(node.right);
    }
}