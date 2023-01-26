package cn.tuyucheng.taketoday.linkedlist.single;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DetectCycleUnitTest {

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithLoop_whenDetectIsExists_thenReturnTrue")
    void givenLinkedListWithLoop_whenDetectIsExists_thenReturnTrue() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.head.next.next = linkedList.head.next;
        boolean isExistsLoop = linkedList.detectLoop(linkedList.head);
        assertTrue(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithNotLoop_whenDetectIsExists_thenReturnFalse")
    void givenLinkedListWithNotLoop_whenDetectIsExists_thenReturnFalse() {
        DetectCycle linkedList = createThreeElementLinkedList();
        boolean isExistsLoop = linkedList.detectLoop(linkedList.head);
        assertFalse(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithLoop_whenDetectIsExistsThroughAddFlag_thenReturnTrue")
    void givenLinkedListWithLoop_whenDetectIsExistsThroughAddFlag_thenReturnTrue() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.head.next.next = linkedList.head.next;
        boolean isExistsLoop = linkedList.detectLoopThroughAddFlag(linkedList.head);
        assertTrue(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithNotLoop_whenDetectIsExistsThroughAddFlag_thenReturnFalse")
    void givenLinkedListWithNotLoop_whenDetectIsExistsThroughAddFlag_thenReturnFalse() {
        DetectCycle linkedList = createThreeElementLinkedList();
        boolean isExistsLoop = linkedList.detectLoopThroughAddFlag(linkedList.head);
        assertFalse(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithLoop_whenDetectIsExistsUsingFloydCycleFinding_thenReturnTrue")
    void givenLinkedListWithLoop_whenDetectIsExistsUsingFloydCycleFinding_thenReturnTrue() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.head.next.next = linkedList.head.next;
        boolean isExistsLoop = linkedList.floydCycleFinding(linkedList.head);
        assertTrue(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithNotLoop_whenDetectIsExistsUsingFloydCycleFinding_thenReturnFalse")
    void givenLinkedListWithNotLoop_whenDetectIsExistsUsingFloydCycleFinding_thenReturnFalse() {
        DetectCycle linkedList = createThreeElementLinkedList();
        boolean isExistsLoop = linkedList.floydCycleFinding(linkedList.head);
        assertFalse(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithLoop_whenDetectIsExistsUsingTempNode_thenReturnTrue")
    void givenLinkedListWithLoop_whenDetectIsExistsUsingTempNode_thenReturnTrue() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.head.next.next = linkedList.head.next;
        boolean isExistsLoop = linkedList.detectLoopUsingTempNode(linkedList.head);
        assertTrue(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithNotLoop_whenDetectIsExistsUsingTempNode_thenReturnFalse")
    void givenLinkedListWithNotLoop_whenDetectIsExistsUsingTempNode_thenReturnFalse() {
        DetectCycle linkedList = createThreeElementLinkedList();
        boolean isExistsLoop = linkedList.detectLoopUsingTempNode(linkedList.head);
        assertFalse(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithLoop_whenDetectIsExistsUsingCount_thenReturnTrue")
    void givenLinkedListWithLoop_whenDetectIsExistsUsingCount_thenReturnTrue() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.head.next.next = linkedList.head.next;
        boolean isExistsLoop = linkedList.detectLoopUsingCount(linkedList.head);
        assertTrue(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithNotLoop_whenDetectIsExistsUsingCount_thenReturnFalse")
    void givenLinkedListWithNotLoop_whenDetectIsExistsUsingCount_thenReturnFalse() {
        DetectCycle linkedList = createThreeElementLinkedList();
        boolean isExistsLoop = linkedList.detectLoopUsingCount(linkedList.head);
        assertFalse(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithLoop_whenDetectIsExistsUsingOtherMethod_thenReturnTrue")
    void givenLinkedListWithLoop_whenDetectIsExistsUsingOtherMethod_thenReturnTrue() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.head.next.next = linkedList.head.next;
        boolean isExistsLoop = linkedList.detectLoopOtherMethod(linkedList.head);
        assertTrue(isExistsLoop);
    }

    @RepeatedTest(10)
    @DisplayName("givenLinkedListWithNotLoop_whenDetectIsExistsUsingOtherMethod_thenReturnFalse")
    void givenLinkedListWithNotLoop_whenDetectIsExistsUsingOtherMethod_thenReturnFalse() {
        DetectCycle linkedList = createThreeElementLinkedList();
        boolean isExistsLoop = linkedList.detectLoopOtherMethod(linkedList.head);
        assertFalse(isExistsLoop);
    }

    @Test
    @DisplayName("givenLinkedListWithLoop_whenDetectedLoopAndCountNode_thenCorrect")
    void givenLinkedListWithLoop_whenDetectedLoopAndCountNode_thenCorrect() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.append(1);
        linkedList.append(2);
        linkedList.append(3);
        linkedList.append(4);
        linkedList.append(5);
        linkedList.head.next.next.next.next.next = linkedList.head.next;
        int loopCount = linkedList.countNodesinLoop(linkedList.head);
        assertEquals(4, loopCount);
    }

    @Test
    @DisplayName("givenLinkedListWithLoop_whenNotDetectedLoopAndCountNode_thenReturnZero")
    void givenLinkedListWithLoop_whenNotDetectedLoopAndCountNode_thenReturnZero() {
        DetectCycle linkedList = createThreeElementLinkedList();
        int loopCount = linkedList.countNodesinLoop(linkedList.head);
        assertEquals(0, loopCount);
    }

    public DetectCycle createThreeElementLinkedList() {
        DetectCycle linkedList = new DetectCycle();
        linkedList.head = new Node(1);
        Node second = new Node(2);
        Node third = new Node(3);
        linkedList.head.next = second;
        second.next = third;
        return linkedList;
    }
}