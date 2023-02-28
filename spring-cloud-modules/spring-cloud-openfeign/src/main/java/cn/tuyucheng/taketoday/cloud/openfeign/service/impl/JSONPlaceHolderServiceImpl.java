package cn.tuyucheng.taketoday.cloud.openfeign.service.impl;

import cn.tuyucheng.taketoday.cloud.openfeign.client.JSONPlaceHolderClient;
import cn.tuyucheng.taketoday.cloud.openfeign.model.Post;
import cn.tuyucheng.taketoday.cloud.openfeign.service.JSONPlaceHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JSONPlaceHolderServiceImpl implements JSONPlaceHolderService {

	@Autowired
	private JSONPlaceHolderClient jsonPlaceHolderClient;

	@Override
	public List<Post> getPosts() {
		return jsonPlaceHolderClient.getPosts();
	}

	@Override
	public Post getPostById(Long id) {
		return jsonPlaceHolderClient.getPostById(id);
	}
}