package cn.tuyucheng.taketoday.algorithms.greedy;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class SocialConnector {
	private boolean isCounterEnabled = true;
	private int counter = 4;
	@Getter
	@Setter
	List<SocialUser> users;

	public SocialConnector() {
		users = new ArrayList<>();
	}

	public boolean switchCounter() {
		this.isCounterEnabled = !this.isCounterEnabled;
		return this.isCounterEnabled;
	}

	public List<SocialUser> getFollowers(String account) {
		if (counter < 0)
			throw new IllegalStateException("API limit reached");
		else {
			if (this.isCounterEnabled) counter--;
			for (SocialUser user : users) {
				if (user.getUsername().equals(account)) {
					return user.getFollowers();
				}
			}
		}
		return new ArrayList<>();
	}
}
