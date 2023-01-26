package cn.tuyucheng.taketoday.tree.traversal;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;

public class DiagonalTraversal {
    Node root;

    public DiagonalTraversal(Node root) {
        this.root = root;
    }

    public void diagonalPrintUsingMap(Node root) {
        TreeMap<Integer, Vector<Integer>> diagonalPrint = new TreeMap<>();
        diagonalPrintUsingMapUtil(root, 0, diagonalPrint);
        for (Map.Entry<Integer, Vector<Integer>> entry : diagonalPrint.entrySet())
            System.out.println(entry.getValue());
    }

    private void diagonalPrintUsingMapUtil(Node root, int d, TreeMap<Integer, Vector<Integer>> diagonalPrint) {
        if (root == null)
            return;
        Vector<Integer> k = diagonalPrint.get(d);
        if (k == null) {
            k = new Vector<>();
            k.add(root.key);
        } else
            k.add(root.key);
        diagonalPrint.put(d, k);
        diagonalPrintUsingMapUtil(root.left, d + 1, diagonalPrint);
        diagonalPrintUsingMapUtil(root.right, d, diagonalPrint);
    }

    public void diagonalPrintUsingIterative(Node root) {
        if (root == null)
            return;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        queue.add(null);
        while (queue.size() > 0) {
            Node current = queue.peek();
            queue.remove();
            if (current == null) {
                if (queue.size() == 0)
                    return;
                System.out.println();
                queue.add(null);
            } else {
                while (current != null) {
                    System.out.print(current.key + " ");
                    if (current.left != null)
                        queue.add(current.left);
                    current = current.right;
                }
            }
        }
    }

    public void diagonalPrintUsingIterativeOptimization(Node root) {
        if (root == null)
            return;
        Node current = root;
        Queue<Node> queue = new LinkedList<>();
        while (!queue.isEmpty() || current != null) {
            if (current != null) {
                System.out.print(current.key + " ");
                if (current.left != null)
                    queue.add(current.left);
                current = current.right;
            } else
                current = queue.remove();
        }
    }
}