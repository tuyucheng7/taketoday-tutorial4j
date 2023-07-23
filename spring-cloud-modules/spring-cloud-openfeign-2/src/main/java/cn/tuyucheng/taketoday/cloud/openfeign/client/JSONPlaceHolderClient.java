package cn.tuyucheng.taketoday.cloud.openfeign.client;

import cn.tuyucheng.taketoday.cloud.openfeign.config.ClientConfiguration;
import cn.tuyucheng.taketoday.cloud.openfeign.hystrix.JSONPlaceHolderFallback;
import cn.tuyucheng.taketoday.cloud.openfeign.model.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "jplaceholder",
	url = "https://jsonplaceholder.typicode.com/",
	configuration = ClientConfiguration.class,
	fallback = JSONPlaceHolderFallback.class)
public interface JSONPlaceHolderClient {

	@RequestMapping(method = RequestMethod.GET, value = "/posts")
	List<Post> getPosts();


	@RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}", produces = "application/json")
	Post getPostById(@PathVariable("postId") Long postId);
}