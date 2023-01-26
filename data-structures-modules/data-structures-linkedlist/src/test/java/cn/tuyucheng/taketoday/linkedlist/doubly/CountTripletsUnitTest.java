package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountTripletsUnitTest {

    @Test
    @DisplayName("givenSortedDoublyLinkedList_whenCountTriplets_thenReturnCorrect")
    void givenSortedDoublyLinkedList_whenCountTriplets_thenReturnCorrect() {
        CountTriplets list = createDoublyLinkedList();
        int x = 17;
        int countTriplets = CountTriplets.countTripletsUsingLoop(list.head, x);
        assertEquals(2, countTriplets);
    }

    @Test
    @DisplayName("givenSortedDoublyLinkedList_whenCountTripletsUsingHash_thenReturnCorrect")
    void givenSortedDoublyLinkedList_whenCountTripletsUsingHash_thenReturnCorrect() {
        CountTriplets list = createDoublyLinkedList();
        int x = 17;
        int countTriplets = CountTriplets.countTripletsUsingHash(list.head, x);
        assertEquals(2, countTriplets);
    }

    @Test
    @DisplayName("givenSortedDoublyLinkedList_whenCountTripletsUsingTwoPointer_thenReturnCorrect")
    void givenSortedDoublyLinkedList_whenCountTripletsUsingTwoPointer_thenReturnCorrect() {
        CountTriplets list = createDoublyLinkedList();
        int x = 17;
        int countTriplets = CountTriplets.countTripletsUsingTwoPointer(list.head, x);
        assertEquals(2, countTriplets);
    }

    public CountTriplets createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(1);
        list.append(2);
        list.append(4);
        list.append(5);
        list.append(6);
        list.append(8);
        list.append(9);
        return new CountTriplets(list);
    }
}