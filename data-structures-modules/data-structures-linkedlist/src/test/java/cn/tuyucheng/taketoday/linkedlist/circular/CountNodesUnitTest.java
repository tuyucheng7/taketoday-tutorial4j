package cn.tuyucheng.taketoday.linkedlist.circular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.linkedlist.circular.CountNodes.countNodes;
import static cn.tuyucheng.taketoday.linkedlist.circular.CountNodes.push;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountNodesUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenCountNumbersOfNodes_thenCorrect")
    void givenLinkedList_whenCountNumbersOfNodes_thenCorrect() {
        Node head = null;
        head = push(head, 12);
        head = push(head, 56);
        head = push(head, 2);
        head = push(head, 11);
        int numberOfNodes = countNodes(head);
        assertEquals(4, numberOfNodes);
    }
}