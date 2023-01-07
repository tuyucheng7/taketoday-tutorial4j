package cn.tuyucheng.taketoday.charstack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharStackUnitTest {

    @Test
    void whenCharStackIsCreated_thenItHasSize0() {

        CharStack charStack = new CharStack();

        assertEquals(0, charStack.size());
    }

    @Test
    void givenEmptyCharStack_whenElementIsPushed_thenStackSizeisIncreased() {

        CharStack charStack = new CharStack();

        charStack.push('A');

        assertEquals(1, charStack.size());
    }

    @Test
    void givenCharStack_whenElementIsPoppedFromStack_thenElementIsRemovedAndSizeChanges() {

        CharStack charStack = new CharStack();
        charStack.push('A');

        char element = charStack.pop();

        assertEquals('A', element);
        assertEquals(0, charStack.size());
    }

    @Test
    void givenCharStack_whenElementIsPeeked_thenElementIsNotRemovedAndSizeDoesNotChange() {
        CharStack charStack = new CharStack();
        charStack.push('A');

        char element = charStack.peek();

        assertEquals('A', element);
        assertEquals(1, charStack.size());
    }
}