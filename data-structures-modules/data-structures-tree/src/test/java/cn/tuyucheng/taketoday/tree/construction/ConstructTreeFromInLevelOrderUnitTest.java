package cn.tuyucheng.taketoday.tree.construction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConstructTreeFromInLevelOrderUnitTest {

    @Test
    @DisplayName("givenInLevelOrderArray_whenConstructTreeFromthem_thenSuccess")
    void givenInLevelOrderArray_whenConstructTreeFromthem_thenSuccess() {
        ConstructTreeFromInLevelOrder builder = new ConstructTreeFromInLevelOrder();
        int[] in = new int[]{4, 8, 10, 12, 14, 20, 22};
        int[] level = new int[]{20, 8, 22, 4, 12, 10, 14};
        ConstructTreeFromInLevelOrder.Node tree = builder.buildTree(in, level);
        builder.printInorder(tree);
    }
}