package cn.tuyucheng.taketoday.scopedvalues.classic;

import cn.tuyucheng.taketoday.scopedvalues.data.Data;
import cn.tuyucheng.taketoday.scopedvalues.data.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class Service {

	private final Repository repository = new Repository();

	public Optional<Data> getData(HttpServletRequest request, User loggedInUser) {
		String id = request.getParameter("data_id");
		return repository.getData(id, loggedInUser);
	}
}