package cn.tuyucheng.taketoday.decorator;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

import static cn.tuyucheng.taketoday.util.LoggerUtil.LOG;

@ExcludeFromJacocoGeneratedReport
public class DecoratorPatternDriver {

	public static void main(String[] args) {
		// christmas tree with just one Garland
		ChristmasTree tree1 = new Garland(new ChristmasTreeImpl());
		LOG.info(tree1.decorate());

		// christmas tree with two Garlands and one Bubble lights
		ChristmasTree tree2 = new BubbleLights(new Garland(new Garland(new ChristmasTreeImpl())));
		LOG.info(tree2.decorate());
	}
}