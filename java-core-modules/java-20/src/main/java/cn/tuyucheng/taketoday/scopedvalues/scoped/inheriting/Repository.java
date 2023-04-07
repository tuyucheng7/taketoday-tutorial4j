package cn.tuyucheng.taketoday.scopedvalues.scoped.inheriting;

import cn.tuyucheng.taketoday.scopedvalues.data.Data;
import cn.tuyucheng.taketoday.scopedvalues.data.User;

import java.util.Optional;

public class Repository {

	public Optional<Data> getData(String id) {
		User loggedInUser = Server.LOGGED_IN_USER.get();
		return loggedInUser.isAdmin()
			? Optional.of(new Data(id, "Title 1", "Description 1"))
			: Optional.empty();
	}
}