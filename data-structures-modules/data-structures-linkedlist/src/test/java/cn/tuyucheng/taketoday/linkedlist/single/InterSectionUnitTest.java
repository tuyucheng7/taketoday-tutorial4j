package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class InterSectionUnitTest {

    @Test
    @DisplayName("givenTwoLinkedList_whenGetInterSection_thenShouldCorrect")
    void givenTwoLinkedList_whenGetInterSection_thenShouldCorrect() {
        InterSection list = new InterSection();
        createTwoLinkedList(list);
        list.sortedIntersectUsingDummyNode();
        list.printList(list.dummy);
    }

    @Test
    @DisplayName("givenTwoLinkedList_whenGetInterSectionUsingRecursive_thenShouldCorrect")
    void givenTwoLinkedList_whenGetInterSectionUsingRecursive_thenShouldCorrect() {
        InterSection list = new InterSection();
        createTwoLinkedList(list);
        Node result = list.sortedIntersectUsingRecursive(list.a, list.b);
        list.printList(result);
    }

    @Test
    @DisplayName("givenTwoLinkedList_whenGetInterSectionUsingHash_thenShouldCorrect")
    void givenTwoLinkedList_whenGetInterSectionUsingHash_thenShouldCorrect() {
        InterSection list = new InterSection();
        createTwoLinkedList(list);
        Integer[] result = list.sortedIntersectUsingHash(list.a, list.b, 2);
        System.out.println(Arrays.toString(result));
    }

    public void createTwoLinkedList(InterSection list) {
        list.a = new Node(1);
        list.a.next = new Node(2);
        list.a.next.next = new Node(3);
        list.a.next.next.next = new Node(4);
        list.a.next.next.next.next = new Node(6);

        list.b = new Node(2);
        list.b.next = new Node(4);
        list.b.next.next = new Node(6);
        list.b.next.next.next = new Node(8);
    }
}