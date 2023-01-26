package cn.tuyucheng.taketoday.tree.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.tree.introduction.EvaluationExpressionTree.Node;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluationExpressionTreeUnitTest {

    @Test
    @DisplayName("givenExpressionTree_whenEvalExpression_thenShouldSuccess")
    void givenExpressionTree_whenEvalExpression_thenShouldSuccess() {
        EvaluationExpressionTree tree = new EvaluationExpressionTree();
        Node root = new Node("+");
        root.left = new Node("*");
        root.left.left = new Node("5");
        root.left.right = new Node("-4");
        root.right = new Node("-");
        root.right.left = new Node("100");
        root.right.right = new Node("20");
        assertEquals(60, tree.evalTree(root));

        root = new Node("+");
        root.left = new Node("*");
        root.left.left = new Node("5");
        root.left.right = new Node("4");
        root.right = new Node("-");
        root.right.left = new Node("100");
        root.right.right = new Node("/");
        root.right.right.left = new Node("20");
        root.right.right.right = new Node("2");
        assertEquals(110, tree.evalTree(root));
    }
}