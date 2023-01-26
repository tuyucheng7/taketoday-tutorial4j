package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoveDuplicateUnitTest {

    @Test
    @DisplayName("givenSortedDoublyLinkedList_whenRemoveDuplicates_thenCorrect")
    void givenSortedDoublyLinkedList_whenRemoveDuplicates_thenCorrect() {
        RemoveDuplicate list = createDoublyLinkedList();
        list.removeDuplicates(list.head);
        list.doublyLinkedList.printList();
    }

    @Test
    @DisplayName("givenUnSortedDoublyLinkedList_whenRemoveDuplicatesUsingLoop_thenCorrect")
    void givenUnSortedDoublyLinkedList_whenRemoveDuplicatesUsingLoop_thenCorrect() {
        RemoveDuplicate list = createUnSortedDoublyLinkedList();
        list.removeDuplicatesInUnsortListUsingLoop(list.head);
        list.doublyLinkedList.printList();
    }

    @Test
    @DisplayName("givenUnSortedDoublyLinkedList_whenRemoveDuplicatesUsingHash_thenCorrect")
    void givenUnSortedDoublyLinkedList_whenRemoveDuplicatesUsingHash_thenCorrect() {
        RemoveDuplicate list = createUnSortedDoublyLinkedList();
        list.removeDuplicatesInUnsortListUsingHash(list.head);
        list.doublyLinkedList.printList();
    }

    public RemoveDuplicate createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(2);
        list.append(2);
        list.append(2);
        list.append(4);
        list.append(5);
        list.append(5);
        list.append(6);
        list.append(8);
        list.append(8);
        return new RemoveDuplicate(list);
    }

    public RemoveDuplicate createUnSortedDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(8);
        list.append(4);
        list.append(4);
        list.append(6);
        list.append(4);
        list.append(8);
        list.append(4);
        list.append(10);
        list.append(12);
        list.append(12);
        return new RemoveDuplicate(list);
    }
}