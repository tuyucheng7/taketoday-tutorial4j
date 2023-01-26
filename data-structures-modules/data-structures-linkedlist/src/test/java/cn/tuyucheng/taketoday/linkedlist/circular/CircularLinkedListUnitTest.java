package cn.tuyucheng.taketoday.linkedlist.circular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CircularLinkedListUnitTest {

    @Test
    @DisplayName("givenEmptyLinkedList_whenAddNodeToEmpty_thenShouldSuccess")
    void givenEmptyLinkedList_whenAddNodeToEmpty_thenShouldSuccess() {
        CircularLinkedList list = new CircularLinkedList();
        Node last = list.addToEmpty(null, 1);
        list.printList(last.next);
    }

    @Test
    @DisplayName("givenLinkedList_whenAddNodeToBeginTwice_thenCorrect")
    void givenLinkedList_whenAddNodeToBeginTwice_thenCorrect() {
        CircularLinkedList list = new CircularLinkedList();
        Node last = list.addBegin(null, 1);
        last = list.addBegin(last, 2);
        list.printList(last.next);
    }

    @Test
    @DisplayName("givenLinkedList_whenAddNodeToEddTwice_thenCorrect")
    void givenLinkedList_whenAddNodeToEddTwice_thenCorrect() {
        CircularLinkedList list = new CircularLinkedList();
        Node last = list.addEnd(null, 1);
        last = list.addEnd(last, 2);
        list.printList(last.next);
    }

    @Test
    @DisplayName("givenLinkedList_whenAddNodeToMiddle_thenCorrect")
    void givenLinkedList_whenAddNodeToMiddle_thenCorrect() {
        CircularLinkedList list = new CircularLinkedList();
        Node last = list.addEnd(null, 1);
        last = list.addEnd(last, 2);
        last = list.addEnd(last, 3);
        last = list.addAfter(last, 4, 2);
        list.printList(last.next);
    }
}