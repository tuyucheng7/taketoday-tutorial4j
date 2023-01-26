package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoveDuplicatesUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenRemoveDuplicates_thenSuccess")
    void givenLinkedList_whenRemoveDuplicates_thenSuccess() {
        RemoveDuplicates linkedList = createSortedLinkedList();
        linkedList.removeDuplicates();
        printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenRemoveDuplicatesRecursive_thenSuccess")
    void givenLinkedList_whenRemoveDuplicatesUsingRecursive_thenSuccess() {
        RemoveDuplicates linkedList = createSortedLinkedList();
        linkedList.removeDuplicate(linkedList.head);
        printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenRemoveDuplicatesUsingOtherMethod_thenSuccess")
    void givenLinkedList_whenRemoveDuplicatesUsingOtherMethod_thenSuccess() {
        RemoveDuplicates linkedList = createSortedLinkedList();
        linkedList.removeDuplicatesUsingOtherMethod();
        printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenRemoveDuplicatesUsingOtherMap_thenSuccess")
    void givenLinkedList_whenRemoveDuplicatesUsingMap_thenSuccess() {
        RemoveDuplicates linkedList = createSortedLinkedList();
        linkedList.removeDuplicatesUsingMap(linkedList.head);
    }

    @Test
    @DisplayName("givenUnsortLinkedList_whenRemoveDuplicatesUsingTwoLoop_thenSuccess")
    void givenUnsortLinkedList_whenRemoveDuplicatesUsingTwoLoop_thenSuccess() {
        RemoveDuplicates linkedList = createUnSortedLinkedList();
        linkedList.removeDeplicatesInUnSortUsingTwoLoop(linkedList.head);
        printList(linkedList.head);
    }

    @Test
    @DisplayName("givenUnsortLinkedList_whenRemoveDuplicatesUsingHash_thenSuccess")
    void givenUnsortLinkedList_whenRemoveDuplicatesUsingHash_thenSuccess() {
        RemoveDuplicates linkedList = createUnSortedLinkedList();
        linkedList.removeDeplicatesInUnSortUsingHash(linkedList.head);
        printList(linkedList.head);
    }

    public RemoveDuplicates createSortedLinkedList() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(1);
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.append(3);
        linkedList.append(4);
        return new RemoveDuplicates(linkedList);
    }

    public RemoveDuplicates createUnSortedLinkedList() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(12);
        linkedList.append(11);
        linkedList.append(12);
        linkedList.append(21);
        linkedList.append(41);
        linkedList.append(43);
        linkedList.append(21);
        return new RemoveDuplicates(linkedList);
    }

    public void printList(Node head) {
        Node n = head;
        while (n != null) {
            System.out.println(n.data);
            n = n.next;
        }
    }
}