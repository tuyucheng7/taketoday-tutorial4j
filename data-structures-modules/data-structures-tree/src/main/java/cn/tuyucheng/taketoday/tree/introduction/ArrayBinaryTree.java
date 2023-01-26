package cn.tuyucheng.taketoday.tree.introduction;

public class ArrayBinaryTree {
    static int root = 0;
    String[] tree = new String[10];

    public void root(String key) {
        tree[0] = key;
    }

    public void setLeft(String key, int root) {
        int t = root * 2 + 1;
        if (tree[root] == null)
            System.out.printf("Can't set child at %d, no parent found\n", t);
        else
            tree[t] = key;
    }

    public void setRight(String key, int root) {
        int t = root * 2 + 2;
        if (tree[root] == null)
            System.out.printf("Can't set child at %d, no parent found\n", t);
        else
            tree[t] = key;
    }

    public void printTree() {
        for (int i = 0; i < 10; i++) {
            if (tree[i] != null)
                System.out.print(tree[i]);
            else
                System.out.print("-");
        }
    }
}