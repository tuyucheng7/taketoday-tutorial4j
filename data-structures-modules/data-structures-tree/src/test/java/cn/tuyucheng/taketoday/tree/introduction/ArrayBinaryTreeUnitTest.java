package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArrayBinaryTreeUnitTest {

    @Test
    @DisplayName("givenArrayTree_whenAddItemsAndPrint_thenCorrect")
    void givenArrayTree_whenAddItemsAndPrint_thenCorrect() {
        ArrayBinaryTree tree = new ArrayBinaryTree();
        tree.root("A");
        tree.setLeft("B", 0);
        tree.setRight("C", 0);
        tree.setLeft("D", 1);
        tree.setRight("E", 1);
        tree.setLeft("F", 2);
        tree.printTree();
    }
}