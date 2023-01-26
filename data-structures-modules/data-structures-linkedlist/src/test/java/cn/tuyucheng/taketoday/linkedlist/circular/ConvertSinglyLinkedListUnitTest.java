package cn.tuyucheng.taketoday.linkedlist.circular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.linkedlist.circular.ConvertSinglyLinkedList.*;

class ConvertSinglyLinkedListUnitTest {

    @Test
    @DisplayName("givenLinkedList_whenConvertToCircular_thenCorrect")
    void givenLinkedList_whenConvertToCircular_thenCorrect() {
        Node head = null;
        head = push(head, 15);
        head = push(head, 14);
        head = push(head, 13);
        head = push(head, 22);
        head = push(head, 17);
        convert(head);
        System.out.print("Display list: \n");
        displayList(head);
    }
}