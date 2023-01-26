package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MergableStackUnitTest {

    @Test
    @DisplayName("givenTwoStack_whenPushSameItemToBoth_thenMergeShouldCorrect")
    void givenTwoStack_whenPushSameItemToBoth_thenMergeShouldCorrect() {
        MergableStack stack1 = new MergableStack();
        MergableStack stack2 = new MergableStack();
        stack1.push(1);
        stack1.push(2);
        stack1.push(3);
        stack2.push(4);
        stack2.push(5);
        stack1.merge(stack2);
        stack1.display();
    }
}