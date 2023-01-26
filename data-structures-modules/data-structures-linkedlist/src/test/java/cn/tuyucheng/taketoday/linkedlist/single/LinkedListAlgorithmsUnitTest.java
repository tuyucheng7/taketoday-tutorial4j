package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkedListAlgorithmsUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenIsPalindromeAndJudge_thenReturnTrue")
    void givenLinkedList_whenIsPalindromeAndJudge_thenReturnTrue() {
        LinkedListAlgorithms linkedList = createPalindromeList();
        boolean isPalindrome = linkedList.isPalindrome(linkedList.head);
        assertTrue(isPalindrome);
    }

    @Test
    @DisplayName("givenLinkedList_whenNotPalindromeAndJudge_thenReturnFalse")
    void givenLinkedList_whenNotPalindromeAndJudge_thenReturnFalse() {
        LinkedListAlgorithms linkedList = createNotPalindromeList();
        boolean isPalindrome = linkedList.isPalindrome(linkedList.head);
        assertFalse(isPalindrome);
    }

    @Test
    @DisplayName("givenLinkedList_whenIsPalindromeAndJudgeUsingReverse_thenReturnTrue")
    void givenLinkedList_whenIsPalindromeAndJudgeUsingReverse_thenReturnTrue() {
        LinkedListAlgorithms linkedList = createPalindromeList();
        boolean isPalindrome = linkedList.isPalindromeUsingReverse(linkedList.head);
        assertTrue(isPalindrome);
    }

    @Test
    @DisplayName("givenLinkedList_whenNotPalindromeAndJudgeUsingReverse_thenReturnFalse")
    void givenLinkedList_whenNotPalindromeAndJudgeUsingReverse_thenReturnFalse() {
        LinkedListAlgorithms linkedList = createNotPalindromeList();
        boolean isPalindrome = linkedList.isPalindromeUsingReverse(linkedList.head);
        assertFalse(isPalindrome);
    }

    @Test
    @DisplayName("givenLinkedList_whenIsPalindromeAndJudgeUsingRecursive_thenReturnTrue")
    void givenLinkedList_whenIsPalindromeAndJudgeUsingRecursive_thenReturnTrue() {
        LinkedListAlgorithms linkedList = createPalindromeList();
        boolean isPalindrome = linkedList.isPalindromeUsingRecursive(linkedList.head);
        assertTrue(isPalindrome);
    }

    @Test
    @DisplayName("givenLinkedList_whenNotPalindromeAndJudgeUsingRecursive_thenReturnFalse")
    void givenLinkedList_whenNotPalindromeAndJudgeUsingRecursive_thenReturnFalse() {
        LinkedListAlgorithms linkedList = createNotPalindromeList();
        boolean isPalindrome = linkedList.isPalindromeUsingRecursive(linkedList.head);
        assertFalse(isPalindrome);
    }

    @Test
    @DisplayName("givenLinkedList_whenMoveLastToFront_thenShouldSuccess")
    void givenLinkedList_whenMoveLastToFront_thenShouldSuccess() {
        LinkedListAlgorithms linkedList = createNotPalindromeList();
        linkedList.moveToFront();
        printList(linkedList.head);
    }

    public LinkedListAlgorithms createPalindromeList() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.append(2);
        linkedList.append(1);
        return new LinkedListAlgorithms(linkedList);
    }

    public LinkedListAlgorithms createNotPalindromeList() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.append(4);
        linkedList.append(1);
        return new LinkedListAlgorithms(linkedList);
    }

    public void printList(Node head) {
        Node n = head;
        while (n != null) {
            System.out.println(n.data);
            n = n.next;
        }
    }
}