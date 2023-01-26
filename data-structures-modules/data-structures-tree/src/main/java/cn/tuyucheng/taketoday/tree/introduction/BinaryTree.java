package cn.tuyucheng.taketoday.tree.introduction;

import static java.lang.System.out;

public class BinaryTree {
    Node root;

    public BinaryTree() {
        root = null;
    }

    public BinaryTree(int key) {
        this.root = new Node(key);
    }

    public void inOrder(Node temp) {
        if (temp == null)
            return;
        inOrder(temp.left);
        out.print(temp.key + " ");
        inOrder(temp.right);
    }
}