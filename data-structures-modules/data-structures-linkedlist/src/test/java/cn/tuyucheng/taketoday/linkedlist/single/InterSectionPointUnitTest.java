package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterSectionPointUnitTest {

    @Test
    @DisplayName("givenTwoLinkedList_whenGetInterSectionPointer_thenReturnCorrect")
    void givenTwoLinkedList_whenGetInterSectionPointer_thenReturnCorrect() {
        InterSectionPoint list1 = createLinkedListOne();
        InterSectionPoint lint2 = createLinkedListTwo();
        int interSectionPoint = InterSectionPoint.getNode(list1.head, lint2.head);
        assertEquals(15, interSectionPoint);
    }

    public InterSectionPoint createLinkedListOne() {
        LinkedList list = new LinkedList();
        list.append(3);
        list.append(6);
        list.append(9);
        list.append(15);
        list.append(30);
        return new InterSectionPoint(list);
    }

    public InterSectionPoint createLinkedListTwo() {
        LinkedList list = new LinkedList();
        list.append(10);
        list.append(15);
        list.append(30);
        return new InterSectionPoint(list);
    }
}