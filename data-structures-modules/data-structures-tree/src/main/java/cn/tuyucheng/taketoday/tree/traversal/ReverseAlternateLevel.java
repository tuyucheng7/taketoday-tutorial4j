package cn.tuyucheng.taketoday.tree.traversal;

public class ReverseAlternateLevel {
    Node root;
    Index indexObj = new Index();

    public ReverseAlternateLevel(Node root) {
        this.root = root;
    }


    class Index {
        int index;
    }
}