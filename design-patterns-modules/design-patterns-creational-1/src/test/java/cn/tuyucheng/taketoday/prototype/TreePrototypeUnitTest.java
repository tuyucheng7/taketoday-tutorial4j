package cn.tuyucheng.taketoday.prototype;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreePrototypeUnitTest {

	@Test
	void givenAPlasticTreePrototypeWhenClonedThenCreateA_Clone() {
		double mass = 10.0;
		double height = 3.7;
		Position position = new Position(3, 7);
		Position otherPosition = new Position(4, 8);

		PlasticTree plasticTree = new PlasticTree(mass, height);
		plasticTree.setPosition(position);
		PlasticTree anotherPlasticTree = (PlasticTree) plasticTree.copy();
		anotherPlasticTree.setPosition(otherPosition);

		assertEquals("Position [x=4, y=8]", anotherPlasticTree.getPosition().toString(), "The position of the clone is not the same as the original!");
		assertEquals("Tree [mass=10.0, height=3.7, position=Position [x=4, y=8]]", anotherPlasticTree.toString(), "The clone is not the same as the original!");
		assertEquals(otherPosition.hashCode(), anotherPlasticTree.getPosition().hashCode());

		assertEquals(position, plasticTree.getPosition());
		assertEquals(otherPosition, anotherPlasticTree.getPosition());
	}

	@Test
	void givenAPineTreePrototypeWhenClonedThenCreateA_Clone() {
		double mass = 10.0;
		double height = 3.7;
		Position position = new Position(3, 7);
		Position otherPosition = new Position(4, 8);

		PineTree pineTree = new PineTree(mass, height);
		pineTree.setPosition(position);
		PineTree anotherPineTree = (PineTree) pineTree.copy();
		anotherPineTree.setPosition(otherPosition);

		assertEquals(position, pineTree.getPosition());
		assertEquals(otherPosition, anotherPineTree.getPosition());
	}

	@Test
	void givenA_ListOfTreesWhenClonedThenCreateListOfClones() {
		double mass = 10.0;
		double height = 3.7;
		Position position = new Position(3, 7);
		Position otherPosition = new Position(4, 8);

		PlasticTree plasticTree = new PlasticTree(mass, height);
		plasticTree.setPosition(position);
		PineTree pineTree = new PineTree(mass, height);
		pineTree.setPosition(otherPosition);

		List<Tree> trees = Arrays.asList(plasticTree, pineTree);

		List<Tree> treeClones = trees.stream().map(Tree::copy).toList();

		Tree plasticTreeClone = treeClones.get(0);

		assertEquals(mass, plasticTreeClone.getMass());
		assertEquals(height, plasticTreeClone.getHeight());
		assertEquals(position, plasticTreeClone.getPosition());
	}
}