package cn.tuyucheng.taketoday.tree.traversal;

public class DepthFromPreOrder {

    public int findDepth(String tree, int n) {
        int index = 0;
        return findDepthRecursive(tree, n, index);
    }

    private int findDepthRecursive(String tree, int n, int index) {
        if (index >= n || tree.charAt(index) == 'l')
            return 0;
        index++;
        int left = findDepthRecursive(tree, n, index);
        index++;
        int right = findDepthRecursive(tree, n, index);
        return Math.max(left, right) + 1;
    }
}