package cn.tuyucheng.taketoday.linkedlist.circular;

public class SplitTwoHalves {
    static Node head, head1, head2;

    void splitList() {
        Node slowPointer = head;
        Node fastPointer = head;
        if (head == null)
            return;
        // 如果循环链表中有奇数节点，则fastPointer.next会在某个时间点变为头节点
        // 对于偶数个节点，fastPointer.next.next会在某个时间点变为头节点
        while (fastPointer.next != head && fastPointer.next.next != head) {
            slowPointer = slowPointer.next;
            fastPointer = fastPointer.next.next;
        }
        // 如果链表中的节点为偶数，则经过以上while循环后，fastPointer指向倒数第二个节点，因此将fastPointer向前移动一次到最后一个节点
        if (fastPointer.next.next == head)
            fastPointer = fastPointer.next;
        // 设置左半部分的头节点
        head1 = head;
        // 设置右半部分的头节点
        if (head.next != head)
            head2 = slowPointer.next;
        // 将左右两部分链表分别修改为循环链表
        fastPointer.next = slowPointer.next;
        slowPointer.next = head;
    }

    void printList(Node node) {
        Node temp = node;
        if (node != null) {
            do {
                System.out.print(temp.data + " ");
                temp = temp.next;
            } while (temp != node);
        }
    }

    static class Node {
        int data;
        Node next, previous;

        public Node(int data) {
            this.data = data;
            next = previous = null;
        }
    }
}