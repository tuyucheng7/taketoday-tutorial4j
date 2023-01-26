package cn.tuyucheng.taketoday.linkedlist.circular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.linkedlist.circular.CircularLinkedListDeletion.*;

class CircularLinkedListDeletionUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenDeletedNode_thenCorrect")
    void givenLinkedList_whenDeletedNode_thenCorrect() {
        Node head = null;
        head = push(head, 2);
        head = push(head, 5);
        head = push(head, 7);
        head = push(head, 8);
        head = push(head, 10);

        System.out.print("List Before Deletion: ");
        printList(head);

        head = deleteNode(head, 7);
        System.out.println();

        System.out.print("List After Deletion: ");
        printList(head);
    }
}