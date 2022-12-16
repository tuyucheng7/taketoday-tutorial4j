package cn.tuyucheng.taketoday.decorator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecoratorPatternIntegrationTest {
	@Test
	void givenDecoratorPattern_WhenDecoratorsInjectedAtRuntime_thenConfigSuccess() {
		ChristmasTree tree1 = new Garland(new ChristmasTreeImpl());
		assertEquals("Christmas tree with Garland", tree1.decorate());

		ChristmasTree tree2 = new BubbleLights(new Garland(new Garland(new ChristmasTreeImpl())));
		assertEquals("Christmas tree with Garland with Garland with Bubble Lights", tree2.decorate());
	}
}