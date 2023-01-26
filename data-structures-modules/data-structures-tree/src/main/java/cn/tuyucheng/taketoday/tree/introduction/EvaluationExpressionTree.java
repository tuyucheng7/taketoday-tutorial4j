package cn.tuyucheng.taketoday.tree.introduction;

public class EvaluationExpressionTree {

    public int toInt(String s) {
        return Integer.parseInt(s);
    }

    public int evalTree(Node root) {
        if (root == null)
            return 0;
        if (root.left == null && root.right == null)
            return toInt(root.data);
        int leftEval = evalTree(root.left);
        int rightEval = evalTree(root.right);
        if (root.data.equals("+"))
            return leftEval + rightEval;
        if (root.data.equals("-"))
            return leftEval - rightEval;
        if (root.data.equals("*"))
            return leftEval * rightEval;
        return leftEval / rightEval;
    }

    static class Node {
        String data;
        Node left;
        Node right;

        public Node(String data) {
            this.data = data;
            left = null;
            right = null;
        }
    }
}