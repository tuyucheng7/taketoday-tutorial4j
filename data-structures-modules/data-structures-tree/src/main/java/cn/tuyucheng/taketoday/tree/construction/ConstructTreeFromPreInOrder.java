package cn.tuyucheng.taketoday.tree.construction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ConstructTreeFromPreInOrder {
    static int preIndex = 0;
    static HashMap<Character, Integer> mp = new HashMap<>();
    static Set<TreeNode> set = new HashSet<>();
    static Stack<TreeNode> stack = new Stack<>();
    Node root;

    public Node buildTree(char[] in, char[] pre, int inStart, int inEnd) {
        if (inStart > inEnd)
            return null;
        Node tNode = new Node(pre[preIndex++]);
        if (inStart == inEnd)
            return tNode;
        int inIndex = search(in, inStart, inEnd, tNode.data);
        tNode.left = buildTree(in, pre, inStart, inIndex - 1);
        tNode.right = buildTree(in, pre, inIndex + 1, inEnd);
        return tNode;
    }

    public int search(char[] arr, int start, int end, char value) {
        int i;
        for (i = start; i <= end; i++) {
            if (arr[i] == value)
                return i;
        }
        return i;
    }

    public Node buildTreeUsingHashWrap(char[] in, char[] pre, int len) {
        for (int i = 0; i < len; i++)
            mp.put(in[i], i);
        return buildTreeUsingHash(in, pre, 0, len - 1);
    }

    private Node buildTreeUsingHash(char[] in, char[] pre, int inStart, int inEnd) {
        if (inStart > inEnd)
            return null;
        char current = pre[preIndex++];
        Node tNode = new Node(current);
        if (inStart == inEnd)
            return tNode;
        Integer inIndex = mp.get(current);
        tNode.left = buildTreeUsingHash(in, pre, inStart, inIndex - 1);
        tNode.right = buildTreeUsingHash(in, pre, inIndex + 1, inEnd);
        return tNode;
    }

    public TreeNode buildTreeUsingStack(int[] preOrder, int[] inOrder) {
        TreeNode root = null;
        for (int pre = 0, in = 0; pre < preOrder.length; ) {
            TreeNode node = null;
            do {
                node = new TreeNode(preOrder[pre]);
                if (root == null)
                    root = node;
                if (!stack.isEmpty()) {
                    if (set.contains(stack.peek())) {
                        set.remove(stack.peek());
                        stack.pop().right = node;
                    } else {
                        stack.peek().left = node;
                    }
                }
                stack.push(node);
            } while (preOrder[pre++] != inOrder[in] && pre < preOrder.length);
            node = null;
            while (!stack.isEmpty() && in < inOrder.length && stack.peek().data == inOrder[in]) {
                node = stack.pop();
                in++;
            }
            if (node != null) {
                set.add(node);
                stack.push(node);
            }
        }
        return root;
    }

    public void printInorder(Node root) {
        if (root == null)
            return;
        printInorder(root.left);
        System.out.print(root.data + " ");
        printInorder(root.right);
    }

    static class Node {
        char data;
        Node left, right;

        Node(char item) {
            data = item;
            left = right = null;
        }
    }

    public static class TreeNode {
        int data;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            data = x;
        }
    }
}