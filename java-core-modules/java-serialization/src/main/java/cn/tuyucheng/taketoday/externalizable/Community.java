package cn.tuyucheng.taketoday.externalizable;

import java.io.Serializable;

public class Community implements Serializable {

	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Community{" +
			"id=" + id +
			'}';
	}
}
