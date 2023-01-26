package cn.tuyucheng.taketoday.linkedlist.doubly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindPairOfSumUnitTest {

    @Test
    @DisplayName("givenDoublyLinkedList_whenFindPairOfSumAndExists_thenCorrect")
    void givenDoublyLinkedList_whenFindPairOfSumAndExists_thenCorrect() {
        FindPairOfSum list = createDoublyLinkedList();
        FindPairOfSum.pairSum(list.head, 7);
    }

    @Test
    @DisplayName("givenDoublyLinkedList_whenFindPairOfSumAndNotExists_thenCorrect")
    void givenDoublyLinkedList_whenFindPairOfSumAndNotExists_thenCorrect() {
        FindPairOfSum list = createDoublyLinkedList();
        FindPairOfSum.pairSum(list.head, 20);
    }

    public FindPairOfSum createDoublyLinkedList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(1);
        list.append(2);
        list.append(4);
        list.append(5);
        list.append(6);
        list.append(8);
        list.append(9);
        return new FindPairOfSum(list);
    }
}