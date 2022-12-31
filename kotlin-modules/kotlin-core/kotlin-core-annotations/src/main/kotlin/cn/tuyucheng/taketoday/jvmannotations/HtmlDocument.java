package cn.tuyucheng.taketoday.jvmannotations;

public class HtmlDocument implements Document {

	@Override
	public String getType() {
		return "HTML";
	}
}