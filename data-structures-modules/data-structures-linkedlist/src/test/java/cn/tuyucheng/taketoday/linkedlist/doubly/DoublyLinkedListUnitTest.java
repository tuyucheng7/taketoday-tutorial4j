package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoublyLinkedListUnitTest {

    @Test
    @DisplayName("givenDoublyLinkedList_whenAddItemInFront_thenShouldSuccess")
    void givenDoublyLinkedList_whenAddItemInFront_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.push(1);
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenAddItemInEnd_thenShouldSuccess")
    void givenDoublyLinkedList_whenAddItemInEnd_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.append(12);
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenAddItemBeforeNode_thenShouldSuccess")
    void givenDoublyLinkedList_whenAddItemBeforeNode_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.insertBefore(list.head.next.next, 6);
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenAddItemAfterNode_thenShouldSuccess")
    void givenDoublyLinkedList_whenAddItemAfterNode_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.insertAfter(list.head.next.next, 6);
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenDeleteNode_thenShouldSuccess")
    void givenDoublyLinkedList_whenDeleteNode_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.deleteNode(list.head.next.next);
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenDeleteNodeAtGivenPosition_thenShouldSuccess")
    void givenDoublyLinkedList_whenDeleteNodeAtGivenPosition_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.deleteNodeAtGivenPosition(1);
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenCalledReverse_thenShouldSuccess")
    void givenDoublyLinkedList_whenCalledReverse_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.reverse();
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenCalledReverseWithStack_thenShouldSuccess")
    void givenDoublyLinkedList_whenCalledReverseWithStack_thenShouldSuccess() {
        DoublyLinkedList list = createDoublyLinkedList();
        list.reverseWithStack();
        list.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenGetItsSize_thenReturnSize")
    void givenDoublyLinkedList_whenGetItsSize_thenReturnSize() {
        DoublyLinkedList list = createDoublyLinkedList();
        int size = list.findSize(list.head);
        assertEquals(4, size);
    }

    public DoublyLinkedList createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.push(2);
        list.push(4);
        list.push(8);
        list.push(10);
        return list;
    }
}