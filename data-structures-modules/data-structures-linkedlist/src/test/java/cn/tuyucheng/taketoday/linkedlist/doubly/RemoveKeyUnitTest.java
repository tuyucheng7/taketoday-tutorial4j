package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoveKeyUnitTest {

    @Test
    @DisplayName("givenDoublyLinkedList_whenRemoveAllNodeEqualsToKey_thenCorrect")
    void givenDoublyLinkedList_whenRemoveAllNodeEqualsToKey_thenCorrect() {
        RemoveKey list = createDoublyLinkedList();
        list.deleteAllOccurOfX(list.head, 5);
        list.doublyLinkedList.printList();
    }

    public RemoveKey createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(2);
        list.append(4);
        list.append(5);
        list.append(5);
        list.append(6);
        list.append(8);
        return new RemoveKey(list);
    }
}