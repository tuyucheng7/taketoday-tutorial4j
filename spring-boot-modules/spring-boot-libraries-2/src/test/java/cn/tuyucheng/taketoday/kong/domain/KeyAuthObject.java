package cn.tuyucheng.taketoday.kong.domain;

/**
 * @author tuyucheng
 */
public class KeyAuthObject {

	public KeyAuthObject(String key) {
		this.key = key;
	}

	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}