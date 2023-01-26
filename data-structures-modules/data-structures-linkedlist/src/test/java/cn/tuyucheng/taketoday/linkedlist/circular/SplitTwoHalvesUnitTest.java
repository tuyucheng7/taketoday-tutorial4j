package cn.tuyucheng.taketoday.linkedlist.circular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.linkedlist.circular.SplitTwoHalves.Node;
import static cn.tuyucheng.taketoday.linkedlist.circular.SplitTwoHalves.*;

class SplitTwoHalvesUnitTest {

    @Test
    @DisplayName("givenCircularLinkedList_whenSplitIntoTwoHalves_thenCorrect")
    void givenCircularLinkedList_whenSplitIntoTwoHalves_thenCorrect() {
        SplitTwoHalves list = new SplitTwoHalves();
        head = new Node(12);
        head.next = new Node(56);
        head.next.next = new Node(2);
        head.next.next.next = new Node(11);
        head.next.next.next.next = head;

        System.out.println("Original Circular Linked list ");
        list.printList(head);

        list.splitList();
        System.out.println();
        System.out.println("First Circular List ");
        list.printList(head1);
        System.out.println();
        System.out.println("Second Circular List ");
        list.printList(head2);
    }
}