package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InsertSortedDoublyLinkedListUnitTest {

    @Test
    @DisplayName("givenSortedDoublyLinkedList_whenInsertNode_thenCorrect")
    void givenSortedDoublyLinkedList_whenInsertNode_thenCorrect() {
        InsertSortedDoublyLinkedList list = createDoublyLinkedList();
        Node head = InsertSortedDoublyLinkedList.sortedInsert(list.head, 3);
        list.doublyLinkedList.printList();
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenInsertToTail_thenSuccess")
    void givenDoublyLinkedList_whenInsertToTail_thenSuccess() {
        InsertSortedDoublyLinkedList list = new InsertSortedDoublyLinkedList(new DoublyLinkedList());
        list.tail = list.head = null;
        list.nodeInsertTail(30);
        list.nodeInsertTail(50);
        list.nodeInsertTail(90);
        list.nodeInsertTail(10);
        list.nodeInsertTail(40);
        list.nodeInsertTail(110);
        list.nodeInsertTail(60);
        list.nodeInsertTail(95);
        list.nodeInsertTail(23);
        InsertSortedDoublyLinkedList.printList(list.head);
    }

    public InsertSortedDoublyLinkedList createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(1);
        list.append(2);
        list.append(4);
        list.append(5);
        return new InsertSortedDoublyLinkedList(list);
    }
}