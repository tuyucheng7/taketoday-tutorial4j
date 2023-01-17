package cn.tuyucheng.taketoday.lambda.todo.api;

import feign.RequestLine;

public interface PostApi {
	@RequestLine("POST /posts")
	void makePost(PostItem item);
}