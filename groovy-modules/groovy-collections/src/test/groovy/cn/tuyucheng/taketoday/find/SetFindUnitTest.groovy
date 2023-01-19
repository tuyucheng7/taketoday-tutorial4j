package cn.tuyucheng.taketoday.find

import org.junit.Test

import static org.junit.Assert.assertTrue

class SetFindUnitTest {

    @Test
    void whenSetContainsElement_thenCheckReturnsTrue() {
        def set = ['a', 'b', 'c'] as Set

        assertTrue(set.contains('a'))
        assertTrue('a' in set)
    }
}