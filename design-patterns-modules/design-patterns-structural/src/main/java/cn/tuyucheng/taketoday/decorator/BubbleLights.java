package cn.tuyucheng.taketoday.decorator;

public class BubbleLights extends TreeDecorator {

	public BubbleLights(ChristmasTree tree) {
		super(tree);
	}

	public String decorate() {
		return super.decorate() + decorateWithBubbleLights();
	}

	private String decorateWithBubbleLights() {
		return " with Bubble Lights";
	}
}