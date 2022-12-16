package cn.tuyucheng.taketoday.decorator;

public abstract class TreeDecorator implements ChristmasTree {
	private ChristmasTree tree;

	protected TreeDecorator(ChristmasTree tree) {
		this.tree = tree;
	}

	@Override
	public String decorate() {
		return tree.decorate();
	}
}