package cn.tuyucheng.taketoday.tree.introduction;

import java.util.Stack;

public class ExpressionTree {
    Node root;

    private boolean isOperator(char ch) {
        return (ch == '+' || ch == '-' || ch == '*' || ch == '/');
    }

    public Node expressionTree(String expression) {
        Stack<Node> stack = new Stack<>();
        Node t1, t2, temp;
        for (int i = 0; i < expression.length(); i++) {
            if (!isOperator(expression.charAt(i))) {
                temp = new Node(expression.charAt(i));
                stack.push(temp);
            } else {
                t1 = stack.pop();
                t2 = stack.pop();
                temp = new Node(expression.charAt(i));
                temp.left = t2;
                temp.right = t1;
                stack.push(temp);
            }
        }
        temp = stack.pop();
        return temp;
    }

    public void inOrder(Node root) {
        if (root == null)
            return;
        inOrder(root.left);
        System.out.print((char) root.key);
        inOrder(root.right);
    }
}