package cn.tuyucheng.taketoday.cloud.openfeign.hystrix;

import cn.tuyucheng.taketoday.cloud.openfeign.client.JSONPlaceHolderClient;
import cn.tuyucheng.taketoday.cloud.openfeign.model.Post;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JSONPlaceHolderFallback implements JSONPlaceHolderClient {

	@Override
	public List<Post> getPosts() {
		return Collections.emptyList();
	}

	@Override
	public Post getPostById(Long postId) {
		return null;
	}
}