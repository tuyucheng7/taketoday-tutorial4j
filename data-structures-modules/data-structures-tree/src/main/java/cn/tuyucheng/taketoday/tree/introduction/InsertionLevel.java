package cn.tuyucheng.taketoday.tree.introduction;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class InsertionLevel {
    Node root;

    public InsertionLevel(Node root) {
        this.root = root;
    }

    public void insert(Node temp, int key) {
        if (temp == null) {
            root = new Node(key);
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(temp);
        while (!queue.isEmpty()) {
            temp = queue.peek();
            queue.remove();
            if (temp.left == null) {
                temp.left = new Node(key);
                return;
            } else
                queue.add(temp.left);
            if (temp.right == null) {
                temp.right = new Node(key);
                return;
            } else
                queue.add(temp.right);
        }
    }

    public void inOrder(Node temp) {
        if (temp == null)
            return;
        inOrder(temp.left);
        System.out.print(temp.key + " ");
        inOrder(temp.right);
    }
}