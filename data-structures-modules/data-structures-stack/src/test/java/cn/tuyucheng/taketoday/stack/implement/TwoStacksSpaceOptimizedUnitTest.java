package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwoStacksSpaceOptimizedUnitTest {

    @Test
    @DisplayName("givenTwoStacks_whenPushTwoStacks_thenSuccess")
    void givenTwoStacks_whenPushTwoStacks_thenSuccess() {
        TwoStacksSpaceOptimized twoStacks = new TwoStacksSpaceOptimized(5);
        twoStacks.push1(5);
        twoStacks.push2(10);
        twoStacks.push2(15);
        twoStacks.push1(11);
        twoStacks.push2(17);
        assertEquals(11, twoStacks.pop1());
        twoStacks.push2(40);
        assertEquals(40, twoStacks.pop2());
    }
}