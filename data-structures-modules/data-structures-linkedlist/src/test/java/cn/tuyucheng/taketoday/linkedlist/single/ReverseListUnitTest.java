package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReverseListUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenReverseListUsingIterative_thenShouldCorrect")
    void givenLinkedList_whenReverseListUsingIterative_thenShouldCorrect() {
        ReverseList linkedList = createLinkedList();
        Node reversedList = linkedList.reverseUsingIterative(linkedList.head);
        printList(reversedList);
    }

    @Test
    @DisplayName("givenLinkedList_whenReverseListUsingRecursive_thenShouldCorrect")
    void givenLinkedList_whenReverseListUsingRecursive_thenShouldCorrect() {
        ReverseList linkedList = createLinkedList();
        Node reversedList = linkedList.reverseUsingRecursive(linkedList.head);
        printList(reversedList);
    }

    @Test
    @DisplayName("givenLinkedList_whenReverseListUsingUtilRecursive_thenShouldCorrect")
    void givenLinkedList_whenReverseListUsingUtilRecursive_thenShouldCorrect() {
        ReverseList linkedList = createLinkedList();
        Node reversedList = linkedList.reverseUtilRecursive(linkedList.head, null);
        printList(reversedList);
    }

    @Test
    @DisplayName("givenLinkedList_whenReverseListUsingStack_thenShouldCorrect")
    void givenLinkedList_whenReverseListUsingStack_thenShouldCorrect() {
        ReverseList linkedList = createLinkedList();
        Node reversedList = linkedList.reverseUsingStack(linkedList.head);
        printList(reversedList);
    }

    public ReverseList createLinkedList() {
        LinkedList list = new LinkedList();
        list.append(1);
        list.append(2);
        list.append(3);
        list.append(4);
        return new ReverseList(list);
    }

    public void printList(Node head) {
        Node n = head;
        while (n != null) {
            System.out.println(n.data);
            n = n.next;
        }
    }
}