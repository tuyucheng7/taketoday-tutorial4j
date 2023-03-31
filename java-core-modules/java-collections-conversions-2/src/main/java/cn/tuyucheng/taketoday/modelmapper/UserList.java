package cn.tuyucheng.taketoday.modelmapper;

import java.util.Collection;

/**
 * UserList class that contain collection of users
 */
public class UserList {
	public UserList() {
	}

	private Collection<User> users;

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}
}