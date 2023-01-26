package cn.tuyucheng.taketoday.linkedlist.single;

import java.util.HashSet;

public class DetectCycle {
    Node head;

    public void append(int newData) {
        Node newNode = new Node(newData);
        if (head == null) {
            head = new Node(newData);
            return;
        }
        newNode.next = null;
        Node last = head;
        while (last.next != null)
            last = last.next;
        last.next = newNode;
    }

    public boolean detectLoop(Node head) {
        HashSet<Node> set = new HashSet<>();
        while (head != null) {
            if (set.contains(head))
                return true;
            set.add(head);
            head = head.next;
        }
        return false;
    }

    public boolean detectLoopThroughAddFlag(Node head) {
        while (head != null) {
            if (head.flag)
                return true;
            head.flag = true;
            head = head.next;
        }
        return false;
    }

    public boolean floydCycleFinding(Node head) {
        Node slowPointer = head;
        Node fastPointer = head;
        while (slowPointer != null && fastPointer != null && fastPointer.next != null) {
            slowPointer = slowPointer.next;
            fastPointer = fastPointer.next.next;
            if (slowPointer == fastPointer)
                return true;
        }
        return false;
    }

    public boolean detectLoopUsingTempNode(Node head) {
        // 创建一个临时节点
        Node temp = new Node(1);
        while (head != null) {
            // 如果当前节点的下一个节点为null，则不存在环
            if (head.next == null)
                return false;
            // 检查next是否已经指向temp
            if (head.next == temp)
                return true;
            // 存储指向下一个节点的指针，以便在下一步中访问它
            Node next = head.next;
            head.next = temp;
            head = next;
        }
        return false;
    }

    public boolean detectLoopUsingCount(Node head) {
        int previousLength = -1;
        int currentLength = 0;
        Node first = head;
        Node last = head;
        while (currentLength > previousLength && last != null) {
            previousLength = currentLength;
            currentLength = distance(first, last);
            last = last.next;
        }
        return last != null;
    }

    private int distance(Node first, Node last) {
        int count = 0;
        Node current = first;
        while (current != last) {
            count++;
            current = current.next;
        }
        return count + 1;
    }

    public boolean detectLoopOtherMethod(Node head) {
        if (head == null)
            return false;
        else {
            while (head != null) {
                if (head.data == -1)
                    return true;
                else {
                    head.data = -1;
                    head = head.next;
                }
            }
        }
        return false;
    }

    public int countNodesinLoop(Node head) {
        Node slowPointer = head;
        Node fastPointer = head;
        int count = 1;
        while (slowPointer != null && fastPointer != null && fastPointer.next != null) {
            slowPointer = slowPointer.next;
            fastPointer = fastPointer.next.next;
            if (slowPointer == fastPointer) {
                Node temp = slowPointer;
                while (temp.next != slowPointer) {
                    count++;
                    temp = temp.next;
                }
                return count;
            }
        }
        return 0;
    }
}