package cn.tuyucheng.taketoday.tree.traversal;

public class FindNthNode {
    static int count = 0;
    Node root;

    public FindNthNode(Node root) {
        this.root = root;
    }

    public void NthInorder(Node node, int n) {
        if (node == null)
            return;
        if (count <= n) {
            NthInorder(node.left, n);
            count++;
            if (count == n)
                System.out.println(node.key);
            NthInorder(node.right, n);
        }
    }

    public void NthPostorder(Node node, int n) {
        if (node == null)
            return;
        if (count < n) {
            NthPostorder(node.left, n);
            NthPostorder(node.right, n);
            count++;
            if (count == n)
                System.out.println(node.key);
        }
    }
}