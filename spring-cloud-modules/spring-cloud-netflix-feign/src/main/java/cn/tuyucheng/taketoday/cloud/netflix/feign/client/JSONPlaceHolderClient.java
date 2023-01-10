package cn.tuyucheng.taketoday.cloud.netflix.feign.client;

import cn.tuyucheng.taketoday.cloud.netflix.feign.config.ClientConfiguration;
import cn.tuyucheng.taketoday.cloud.netflix.feign.hystrix.JSONPlaceHolderFallback;
import cn.tuyucheng.taketoday.cloud.netflix.feign.model.Post;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "jplaceholder",
      url = "${external.api.url}",
      configuration = ClientConfiguration.class,
      fallback = JSONPlaceHolderFallback.class)
public interface JSONPlaceHolderClient {

    @RequestMapping(method = RequestMethod.GET, value = "/posts")
    List<Post> getPosts();

    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}", produces = "application/json")
    Post getPostById(@PathVariable("postId") Long postId);

}
