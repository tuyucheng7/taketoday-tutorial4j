package cn.tuyucheng.taketoday.stack_tests;

import cn.tuyucheng.taketoday.thread_safe_lifo.DequeBasedSynchronizedStack;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * These tests are to understand the Stack implementation in Java Collections.
 */
public class StackUnitTest {

    @Test
    public void givenStack_whenPushPopPeek_thenWorkRight() {
        Stack<String> namesStack = new Stack<>();

        namesStack.push("Bill Gates");
        namesStack.push("Elon Musk");

        Assert.assertEquals("Elon Musk", namesStack.peek());
        Assert.assertEquals("Elon Musk", namesStack.pop());
        Assert.assertEquals("Bill Gates", namesStack.pop());

        Assert.assertEquals(0, namesStack.size());
    }

    @Test
    public void givenSynchronizedDeque_whenPushPopPeek_thenWorkRight() {
        DequeBasedSynchronizedStack<String> namesStack = new DequeBasedSynchronizedStack<>();

        namesStack.push("Bill Gates");
        namesStack.push("Elon Musk");

        Assert.assertEquals("Elon Musk", namesStack.peek());
        Assert.assertEquals("Elon Musk", namesStack.pop());
        Assert.assertEquals("Bill Gates", namesStack.pop());

        Assert.assertEquals(0, namesStack.size());
    }

    @Test
    public void givenConcurrentLinkedDeque_whenPushPopPeek_thenWorkRight() {
        ConcurrentLinkedDeque<String> namesStack = new ConcurrentLinkedDeque<>();

        namesStack.push("Bill Gates");
        namesStack.push("Elon Musk");

        Assert.assertEquals("Elon Musk", namesStack.peek());
        Assert.assertEquals("Elon Musk", namesStack.pop());
        Assert.assertEquals("Bill Gates", namesStack.pop());

        Assert.assertEquals(0, namesStack.size());
    }
}