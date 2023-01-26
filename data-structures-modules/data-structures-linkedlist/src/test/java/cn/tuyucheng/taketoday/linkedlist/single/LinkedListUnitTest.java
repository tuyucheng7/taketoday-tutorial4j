package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListUnitTest {

    @Test
    @DisplayName("givenLinkedListWithThreeItem_whenReversal_thenPrintData")
    void givenLinkedListWithThreeItem_whenReversal_thenPrintData() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenInsertItemInFront_thenShouldSuccess")
    void givenLinkedList_whenInsertItemInFront_thenShouldSuccess() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.push(4);
        linkedList.printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenAppendItem_thenShouldSuccess")
    void givenLinkedList_whenAppendItem_thenShouldSuccess() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenInsertItemAfterGivenNode_thenShouldSuccess")
    void givenLinkedList_whenInsertItemAfterGivenNode_thenShouldSuccess() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.insertAfter(linkedList.head.next, 8);
        linkedList.printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenDeleteAnItem_thenShouldSuccess")
    void givenLinkedList_whenDeleteAnItem_thenShouldSuccess() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.delete(2);
        linkedList.printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenDeleteNodeAtAPosition_thenShouldSuccess")
    void givenLinkedList_whenDeleteNodeAtAPosition_thenShouldSuccess() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.deleteNode(1);
        linkedList.printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetCountUsingIterative_thenCorrectCount")
    void givenLinkedList_whenGetCountUsingIterative_thenCorrectCount() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        int count = linkedList.getCountUsingIterative();
        assertEquals(4, count);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetCountUsingRecursive_thenCorrectCount")
    void givenLinkedList_whenGetCountUsingRecursive_thenCorrectCount() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        int count = linkedList.getCountUsingIterative();
        assertEquals(4, count);
    }

    @Test
    @DisplayName("givenLinkedList_whenSearchItemUsingIterativeAndExists_thenReturnTrue")
    void givenLinkedList_whenSearchItemUsingIterativeAndExists_thenReturnTrue() {
        LinkedList linkedList = createThreeElementLinkedList();
        boolean isExists = linkedList.searchUsingIterative(linkedList.head, 3);
        assertTrue(isExists);
    }

    @Test
    @DisplayName("givenLinkedList_whenSearchItemUsingIterativeAndNotExists_thenReturnFalse")
    void givenLinkedList_whenSearchItemUsingIterativeAndNotExists_thenReturnFalse() {
        LinkedList linkedList = createThreeElementLinkedList();
        boolean isExists = linkedList.searchUsingIterative(linkedList.head, 4);
        assertFalse(isExists);
    }

    @Test
    @DisplayName("givenLinkedList_whenSearchItemUsingRecursiveAndExists_thenReturnTrue")
    void givenLinkedList_whenSearchItemUsingRecursiveAndExists_thenReturnTrue() {
        LinkedList linkedList = createThreeElementLinkedList();
        boolean isExists = linkedList.searchUsingIterative(linkedList.head, 3);
        assertTrue(isExists);
    }

    @Test
    @DisplayName("givenLinkedList_whenSearchItemUsingRecursiveAndNotExists_thenReturnFalse")
    void givenLinkedList_whenSearchItemUsingRecursiveAndNotExists_thenReturnFalse() {
        LinkedList linkedList = createThreeElementLinkedList();
        boolean isExists = linkedList.searchUsingIterative(linkedList.head, 4);
        assertFalse(isExists);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetNthItemAndExists_thenCorrect")
    void givenLinkedList_whenGetNthItemAndExists_thenCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        int returnVaule = linkedList.GetNth(2);
        assertEquals(3, returnVaule);
    }

    @Test
    @Disabled
    @DisplayName("givenLinkedList_whenGetNthItemAndNotExists_thenFail")
    void givenLinkedList_whenGetNthItemAndNotExists_thenFail() {
        LinkedList linkedList = createThreeElementLinkedList();
        int returnValue = linkedList.GetNth(4);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetNthUsingRecursiveItemAndExists_thenCorrect")
    void givenLinkedList_whenGetNthItemUsingRecursiveAndExists_thenCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        int returnVaule = linkedList.GetNth(2);
        assertEquals(3, returnVaule);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetNthFromLast_thenReturnCorrect")
    void givenLinkedList_whenGetNthFromLast_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        int returnValue = linkedList.getNthFromLast(4);
        assertEquals(2, returnValue);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetNthFromLastUsingTwoPointer_thenReturnCorrect")
    void givenLinkedList_whenGetNthFromLastUsingTwoPointer_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        int returnValue = linkedList.getNthFromLast(1);
        assertEquals(5, returnValue);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetMiddleNodeUsingIterative_thenReturnCorrect")
    void givenLinkedList_whenGetMiddleNodeUsingIterative_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        int returnValue = linkedList.getMiddleNodeUsingRecursive(linkedList.head);
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("givenLinkedListWithEvenSize_whenGetMiddleNodeUsingIterative_thenReturnCorrect")
    void givenLinkedListWithEvenSize_whenGetMiddleNodeUsingIterative_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        linkedList.append(6);
        int returnValue = linkedList.getMiddleNodeUsingRecursive(linkedList.head);
        assertEquals(4, returnValue);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetMiddleNodeUsingTwoPointer_thenReturnCorrect")
    void givenLinkedList_whenGetMiddleNodeUsingTwoPointer_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        int returnValue = linkedList.getMiddleNodeUsingRecursive(linkedList.head);
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("givenLinkedListWithEvenSize_whenGetMiddleNodeUsingTwoPointer_thenReturnCorrect")
    void givenLinkedListWithEvenSize_whenGetMiddleNodeUsingTwoPointer_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        linkedList.append(6);
        int returnValue = linkedList.getMiddleNodeUsingRecursive(linkedList.head);
        assertEquals(4, returnValue);
    }

    @Test
    @DisplayName("givenLinkedList_whenGetMiddleNodeUsingOtherMethod_thenReturnCorrect")
    void givenLinkedList_whenGetMiddleNodeUsingOtherMethod_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        int returnValue = linkedList.getMiddleNodeUsingRecursive(linkedList.head);
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("givenLinkedListWithEvenSize_whenGetMiddleNodeUsingOtherMethod_thenReturnCorrect")
    void givenLinkedListWithEvenSize_whenGetMiddleNodeUsingOtherMethod_thenReturnCorrect() {
        LinkedList linkedList = createThreeElementLinkedList();
        linkedList.append(4);
        linkedList.append(5);
        linkedList.append(6);
        int returnValue = linkedList.getMiddleNodeUsingRecursive(linkedList.head);
        assertEquals(4, returnValue);
    }

    @Test
    @DisplayName("givenLinkedList_whenCountItemOccursTimes_thenReturnCount")
    void givenLinkedList_whenCountItemOccursTimes_thenReturnCount() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(3);
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(2);
        int count = linkedList.countOccursTime(2);
        assertEquals(2, count);
    }

    @Test
    @DisplayName("givenLinkedList_whenCountItemOccursTimesWithGlobalVal_thenReturnCount")
    void givenLinkedList_whenCountItemOccursTimesWithGlobalVal_thenReturnCount() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(3);
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(2);
        int count = linkedList.countOccursTimeUsingRecursiveWithGloabl(linkedList.head, 2);
        assertEquals(2, count);
    }

    @Test
    @DisplayName("givenLinkedList_whenCountItemOccursTimesUsingRecursive_thenReturnCount")
    void givenLinkedList_whenCountItemOccursTimesUsingRecursive_thenReturnCount() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(3);
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(2);
        int count = LinkedList.countOccursTimeUsingRecursive(linkedList.head, 2);
        assertEquals(2, count);
    }

    public LinkedList createThreeElementLinkedList() {
        LinkedList linkedList = new LinkedList();
        linkedList.head = new Node(1);
        Node second = new Node(2);
        Node third = new Node(3);
        linkedList.head.next = second;
        second.next = third;
        return linkedList;
    }
}