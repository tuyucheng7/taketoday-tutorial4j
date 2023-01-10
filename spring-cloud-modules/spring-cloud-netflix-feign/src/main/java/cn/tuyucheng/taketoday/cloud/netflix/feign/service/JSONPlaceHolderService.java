package cn.tuyucheng.taketoday.cloud.netflix.feign.service;

import cn.tuyucheng.taketoday.cloud.netflix.feign.model.Post;

import java.util.List;

public interface JSONPlaceHolderService {

    List<Post> getPosts();

    Post getPostById(Long id);
}
