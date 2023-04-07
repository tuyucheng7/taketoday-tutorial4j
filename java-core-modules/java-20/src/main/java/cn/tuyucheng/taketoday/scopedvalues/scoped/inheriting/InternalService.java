package cn.tuyucheng.taketoday.scopedvalues.scoped.inheriting;

import cn.tuyucheng.taketoday.scopedvalues.data.Data;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class InternalService {

	private final Repository repository = new Repository();

	public Optional<Data> getData(HttpServletRequest request) {
		String id = request.getParameter("data_id");
		return repository.getData(id);
	}
}