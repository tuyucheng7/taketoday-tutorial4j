package cn.tuyucheng.taketoday.springpagination.service;

import cn.tuyucheng.taketoday.springpagination.model.Post;

import java.util.List;

public interface IPostService {

	List<Post> getPostsList(int page, int size, String sortDir, String sort);

	void updatePost(Post post);

	Post createPost(Post post);

	Post getPostById(Long id);

}
