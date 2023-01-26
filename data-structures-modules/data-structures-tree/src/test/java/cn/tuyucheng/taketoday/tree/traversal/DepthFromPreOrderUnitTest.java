package cn.tuyucheng.taketoday.tree.traversal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepthFromPreOrderUnitTest {

    @Test
    @DisplayName("givenPreOrderString_whenComputeDepth_thenReturnCorrect")
    void givenPreOrderString_whenComputeDepth_thenReturnCorrect() {
        String tree = "nlnnlll";
        int n = tree.length();
        DepthFromPreOrder util = new DepthFromPreOrder();
        int depth = util.findDepth(tree, n);
        assertEquals(3, depth);
    }
}