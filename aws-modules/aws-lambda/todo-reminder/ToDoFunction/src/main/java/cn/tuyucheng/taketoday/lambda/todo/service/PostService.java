package cn.tuyucheng.taketoday.lambda.todo.service;

import cn.tuyucheng.taketoday.lambda.todo.api.PostApi;
import cn.tuyucheng.taketoday.lambda.todo.api.PostItem;
import cn.tuyucheng.taketoday.lambda.todo.api.ToDoItem;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PostService {
	private static final Logger LOGGER = LogManager.getLogger(PostService.class);
	private PostApi postApi;

	@Inject
	public PostService(PostApi postApi) {
		this.postApi = postApi;
	}

	public void makePost(ToDoItem toDoItem) {
		LOGGER.info("Posting about: {}", toDoItem);
		PostItem item = new PostItem();
		item.setTitle("To Do is Out Of Date: " + toDoItem.getId());
		item.setUserId(toDoItem.getUserId());
		item.setBody("Not done: " + toDoItem.getTitle());

		LOGGER.info("Post: {}", item);
		postApi.makePost(item);
	}
}