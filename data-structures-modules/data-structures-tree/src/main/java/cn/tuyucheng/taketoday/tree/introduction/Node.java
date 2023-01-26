package cn.tuyucheng.taketoday.tree.introduction;

public class Node {
    int key;
    Node left;
    Node right;

    public Node(int key) {
        this.key = key;
        left = null;
        right = null;
    }
}