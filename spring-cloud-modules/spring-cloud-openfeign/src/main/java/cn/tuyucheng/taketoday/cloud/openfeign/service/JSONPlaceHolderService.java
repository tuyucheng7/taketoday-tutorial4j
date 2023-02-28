package cn.tuyucheng.taketoday.cloud.openfeign.service;

import cn.tuyucheng.taketoday.cloud.openfeign.model.Post;

import java.util.List;

public interface JSONPlaceHolderService {

	List<Post> getPosts();

	Post getPostById(Long id);
}