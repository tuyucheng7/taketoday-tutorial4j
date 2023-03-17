package cn.tuyucheng.taketoday.fluentinterface.components;

public class HorizontalLine implements HtmlElement {
	@Override
	public String html() {
		return "<hr>";
	}
}