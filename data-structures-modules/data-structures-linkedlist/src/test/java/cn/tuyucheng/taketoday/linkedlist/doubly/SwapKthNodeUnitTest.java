package cn.tuyucheng.taketoday.linkedlist.doubly;

import cn.tuyucheng.taketoday.linkedlist.single.LinkedList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SwapKthNodeUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenSwapKthNode_thenShouldCorrect")
    void givenLinkedList_whenSwapKthNode_thenShouldCorrect() {
        SwapKthNode list = createLinkedList();
        list.swapKth(2);
        list.linkedList.printList(list.head);
    }

    public SwapKthNode createLinkedList() {
        LinkedList list = new LinkedList();
        list.append(1);
        list.append(2);
        list.append(3);
        list.append(4);
        list.append(5);
        return new SwapKthNode(list);
    }
}