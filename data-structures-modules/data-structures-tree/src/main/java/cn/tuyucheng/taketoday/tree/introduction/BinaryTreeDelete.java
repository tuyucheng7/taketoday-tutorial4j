package cn.tuyucheng.taketoday.tree.introduction;

import java.util.LinkedList;
import java.util.Queue;

public class BinaryTreeDelete {
    Node root;

    public BinaryTreeDelete(Node root) {
        this.root = root;
    }

    public Node deleteNode(Node root, int key) {
        if (root == null)
            return null;
        if (root.left == null && root.right == null) {
            if (root.key == key)
                return null;
            return root;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        Node keyNode = null;
        Node temp = null;
        Node last = null;
        while (!queue.isEmpty()) {
            temp = queue.peek();
            queue.remove();
            if (temp.key == key)
                keyNode = temp;
            if (temp.left != null) {
                last = temp;
                queue.add(temp.left);
            }
            if (temp.right != null) {
                last = temp;
                queue.add(temp.right);
            }
        }
        if (keyNode != null) {
            int x = temp.key;
            if (last.right == temp)
                last.right = null;
            else
                last.left = null;
            keyNode.key = x;
        }
        return root;
    }

    // public void deleteNode(Node root, int key) {
    //   if (root == null)
    //     return;
    //   if (root.left == null && root.right == null) {
    //     if (root.key == key)
    //       root = null;
    //     return;
    //   }
    //   Queue<Node> queue = new LinkedList<>();
    //   queue.add(root);
    //   Node temp = null;
    //   Node keyNode = null;
    //   while (!queue.isEmpty()) {
    //     temp = queue.peek();
    //     queue.remove();
    //     if (temp.key == key)
    //       keyNode = temp;
    //     if (temp.left != null)
    //       queue.add(temp.left);
    //     if (temp.right != null)
    //       queue.add(temp.right);
    //   }
    //   if (keyNode != null) {
    //     int x = temp.key;
    //     deleteDeepest(root, temp);
    //     keyNode.key = x;
    //   }
    // }
    //
    // private void deleteDeepest(Node root, Node deletedNode) {
    //   Queue<Node> queue = new LinkedList<>();
    //   queue.add(root);
    //   Node temp;
    //   while (!queue.isEmpty()) {
    //     temp = queue.peek();
    //     queue.remove();
    //     if (temp == deletedNode) {
    //       temp = null;
    //       break;
    //     }
    //     if (temp.right != null) {
    //       if (temp.right == deletedNode) {
    //         temp.right = null;
    //         return;
    //       } else
    //         queue.add(temp.right);
    //     }
    //     if (temp.left != null) {
    //       if (temp.left == deletedNode) {
    //         temp.left = null;
    //         return;
    //       } else
    //         queue.add(temp.left);
    //     }
    //   }
    // }

    public void inOrder(Node root) {
        if (root == null)
            return;
        inOrder(root.left);
        System.out.print(root.key + " ");
        inOrder(root.right);
    }
}