package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SwapNodeUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenSwapTwoNode_thenSuccess")
    void givenLinkedList_whenSwapTwoNode_thenSuccess() {
        SwapNode linkedList = createLinkedList();
        linkedList.swapNodes(2, 4);
        printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenPairWiseSwapNodeUsingIterative_thenSuccess")
    void givenLinkedList_whenPairWiseSwapNodeUsingIterative_thenSuccess() {
        SwapNode linkedList = createLinkedList();
        linkedList.pairWiseSwapUsingIterative(linkedList.head);
        printList(linkedList.head);
    }

    @Test
    @DisplayName("givenLinkedList_whenPairWiseSwapNodeUsingRecursive_thenSuccess")
    void givenLinkedList_whenPairWiseSwapNodeUsingRecursive_thenSuccess() {
        SwapNode linkedList = createLinkedList();
        linkedList.pairWiseSwapUsingRecursive(linkedList.head);
        printList(linkedList.head);
    }

    public SwapNode createLinkedList() {
        LinkedList linkedList = new LinkedList();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.append(4);
        linkedList.append(5);
        return new SwapNode(linkedList);
    }

    public void printList(Node head) {
        Node n = head;
        while (n != null) {
            System.out.println(n.data);
            n = n.next;
        }
    }
}