package cn.tuyucheng.taketoday.linkedlist.single;

public class InterSectionPoint {
    LinkedList linkedList;
    Node head;

    public InterSectionPoint(LinkedList linkedList) {
        this.linkedList = linkedList;
        head = linkedList.head;
    }

    public static int getNode(Node head1, Node head2) {
        int c1 = getCount(head1);
        int c2 = getCount(head2);
        int d;
        if (c1 < c2) {
            d = c2 - c1;
            return getInterSectionNode(head1, head2, d);
        } else {
            d = c1 - c2;
            return getInterSectionNode(head2, head1, d);
        }
    }

    private static int getInterSectionNode(Node head1, Node head2, int d) {
        Node temp1 = head2;
        Node temp2 = head1;
        int count = 0;
        while (count < d) {
            count++;
            temp1 = temp1.next;
        }
        while (temp1 != null && temp2 != null) {
            if (temp1.data == temp2.data)
                return temp1.data;
            temp1 = temp1.next;
            temp2 = temp2.next;
        }
        return -1;
    }

    private static int getCount(Node head) {
        Node current = head;
        int count = 0;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }
}