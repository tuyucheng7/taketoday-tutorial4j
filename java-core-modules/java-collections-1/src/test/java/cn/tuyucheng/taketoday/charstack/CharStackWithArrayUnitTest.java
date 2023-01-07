package cn.tuyucheng.taketoday.charstack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharStackWithArrayUnitTest {

    @Test
    void whenCharStackIsCreated_thenItHasSize0() {
        CharStackWithArray charStack = new CharStackWithArray();

        assertEquals(0, charStack.size());
    }

    @Test
    void givenEmptyCharStack_whenElementIsPushed_thenStackSizeisIncreased() {
        CharStackWithArray charStack = new CharStackWithArray();

        charStack.push('A');

        assertEquals(1, charStack.size());
    }

    @Test
    void givenEmptyCharStack_when5ElementIsPushed_thenStackSizeis() {
        CharStackWithArray charStack = new CharStackWithArray();

        charStack.push('A');
        charStack.push('B');
        charStack.push('C');
        charStack.push('D');
        charStack.push('E');

        assertEquals(5, charStack.size());
    }

    @Test
    void givenCharStack_whenElementIsPoppedFromStack_thenElementIsRemovedAndSizeChanges() {
        CharStackWithArray charStack = new CharStackWithArray();
        charStack.push('A');

        char element = charStack.pop();

        assertEquals('A', element);
        assertEquals(0, charStack.size());
    }

    @Test
    void givenCharStack_whenElementIsPeeked_thenElementIsNotRemovedAndSizeDoesNotChange() {
        CharStackWithArray charStack = new CharStackWithArray();
        charStack.push('A');

        char element = charStack.peek();

        assertEquals('A', element);
        assertEquals(1, charStack.size());
    }
}