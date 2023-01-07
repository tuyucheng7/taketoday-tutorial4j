package cn.tuyucheng.taketoday.arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaxArraySizeUnitTest {

    @Test
    void whenInitialArrayMoreThanMaxSize_thenThrowArray() {
        boolean initalized = false;
        try {
            int[] arr = new int[Integer.MAX_VALUE - 1];
            initalized = true;
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("Requested array size exceeds VM limit"));
        }
        assertFalse(initalized);
    }

    @Test
    void whenInitialArrayLessThanMaxSize_thenThrowArray() {
        int[] arr = null;
        try {
            arr = new int[Integer.MAX_VALUE - 2];
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("Java heap space"));
        }
    }
}