package cn.tuyucheng.taketoday.tree.introduction;

public class SymmetricTree {
    Node root;

    // 检查树是否是自身的镜像
    public boolean isSymmetirc() {
        return isMirror(root, root);
    }

    // 如果根节点为node1和node2的树互为镜像，则返回true
    private boolean isMirror(Node node1, Node node2) {
        // 如果两个树都为空，返回true
        if (node1 == null && node2 == null)
            return true;
        // For two trees to be mirror images, the following three conditions must be true
        // 1.) Their root node's key must be same
        // 2.) left subtree of left tree and right subtree
        // of right tree have to be mirror images
        // 3.) right subtree of left tree and left subtree
        // of right tree have to be mirror images
        if (node1 != null && node2 != null && node1.key == node2.key)
            return isMirror(node1.left, node2.right) && isMirror(node1.right, node2.left);
        return false;
    }
}