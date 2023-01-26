package cn.tuyucheng.taketoday.linkedlist.circular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.linkedlist.circular.CheckIfLinkedListIsCircular.isCircular;
import static cn.tuyucheng.taketoday.linkedlist.circular.CheckIfLinkedListIsCircular.newNode;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckIfLinkedListIsCircularUnitTest {

    @Test
    @DisplayName("givenCircularLinkedList_whenCheckIfCircular_thenReturnTrue")
    void givenCircularLinkedList_whenCheckIfCircular_thenReturnTrue() {
        Node head = newNode(1);
        head.next = newNode(2);
        head.next.next = newNode(3);
        head.next.next.next = newNode(4);
        head.next.next.next.next = head;
        assertTrue(isCircular(head));
    }

    @Test
    @DisplayName("givenSingleLinkedList_whenCheckIfCircular_thenReturnFalse")
    void givenSingleLinkedList_whenCheckIfCircular_thenReturnFalse() {
        Node head = newNode(1);
        head.next = newNode(2);
        head.next.next = newNode(3);
        head.next.next.next = newNode(4);
        assertFalse(isCircular(head));
    }
}