package cn.tuyucheng.taketoday.kong.domain;

/**
 * @author tuyucheng
 */
public class UpstreamObject {

	public UpstreamObject(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}