package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpressionTreeUnitTest {

    @Test
    @DisplayName("givenExpressionTree_whenPrintExpression_thenCorrect")
    void givenExpressionTree_whenPrintExpression_thenCorrect() {
        ExpressionTree tree = new ExpressionTree();
        Node root = tree.expressionTree("ABC*+D/");
        tree.inOrder(root);
    }
}