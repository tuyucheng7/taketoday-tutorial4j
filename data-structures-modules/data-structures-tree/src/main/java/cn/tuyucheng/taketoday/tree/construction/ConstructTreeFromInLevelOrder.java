package cn.tuyucheng.taketoday.tree.construction;

public class ConstructTreeFromInLevelOrder {
    Node root;

    public Node buildTree(int[] in, int[] level) {
        return constructTree(null, level, in, 0, in.length - 1);
    }

    private Node constructTree(Node startNode, int[] levelOrder, int[] inOrder, int inStart, int inEnd) {
        if (inStart > inEnd)
            return null;
        if (inStart == inEnd)
            return new Node(inOrder[inStart]);
        boolean found = false;
        int index = 0;
        for (int i = 0; i < levelOrder.length - 1; i++) {
            int data = levelOrder[i];
            for (int j = inStart; j < inEnd; j++) {
                if (data == inOrder[j]) {
                    startNode = new Node(data);
                    found = true;
                    index = j;
                    break;
                }
            }
            if (found)
                break;
        }
        startNode.setLeft(constructTree(startNode, levelOrder, inOrder, inStart, index - 1));
        startNode.setRight(constructTree(startNode, levelOrder, inOrder, index + 1, inEnd));
        return startNode;
    }

    public void printInorder(Node node) {
        if (node == null)
            return;
        printInorder(node.left);
        System.out.print(node.data + " ");
        printInorder(node.right);
    }

    static class Node {
        int data;
        Node left, right;

        Node(int item) {
            data = item;
            left = right = null;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public void setRight(Node right) {
            this.right = right;
        }
    }
}