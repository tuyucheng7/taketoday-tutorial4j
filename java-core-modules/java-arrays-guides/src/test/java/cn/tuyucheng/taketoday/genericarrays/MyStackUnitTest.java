package cn.tuyucheng.taketoday.genericarrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MyStackUnitTest {

    @Test
    void givenStackWithTwoItems_whenPop_thenReturnLastAdded() {
        MyStack<String> myStack = new MyStack<>(String.class, 2);
        myStack.push("hello");
        myStack.push("example");

        assertEquals("example", myStack.pop());
    }

    @Test
    void givenStackWithFixedCapacity_whenExceedCapacity_thenThrowException() {
        MyStack<Integer> myStack = new MyStack<>(Integer.class, 2);
        myStack.push(100);
        myStack.push(200);
        assertThrows(RuntimeException.class, () -> myStack.push(300));
    }

    @Test
    void givenStack_whenPopOnEmptyStack_thenThrowException() {
        MyStack<Integer> myStack = new MyStack<>(Integer.class, 1);
        myStack.push(100);
        myStack.pop();
        assertThrows(RuntimeException.class, myStack::pop);
    }

    @Test
    void givenStackWithItems_whenGetAllElements_thenSizeShouldEqualTotal() {
        MyStack<String> myStack = new MyStack<>(String.class, 2);
        myStack.push("hello");
        myStack.push("example");

        String[] items = myStack.getAllElements();

        assertEquals(2, items.length);
    }
}