package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RotateDoublyLinkedListUnitTest {

    @Test
    @DisplayName("givenDoublyLinkedList_whenLeftRotate3Times_thenCorrect")
    void givenDoublyLinkedList_whenLeftRotate3Times_thenCorrect() {
        RotateDoublyLinkedList list = createDoublyLinkedList();
        list.rotate(3);
        list.doublyLinkedList.printList();
    }

    public RotateDoublyLinkedList createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(1);
        list.append(2);
        list.append(4);
        list.append(5);
        list.append(6);
        list.append(8);
        list.append(9);
        return new RotateDoublyLinkedList(list);
    }
}