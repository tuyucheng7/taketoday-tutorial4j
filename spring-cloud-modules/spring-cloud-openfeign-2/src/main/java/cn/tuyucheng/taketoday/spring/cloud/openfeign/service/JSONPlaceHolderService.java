package cn.tuyucheng.taketoday.spring.cloud.openfeign.service;

import cn.tuyucheng.taketoday.spring.cloud.openfeign.model.Post;

import java.util.List;

public interface JSONPlaceHolderService {

   List<Post> getPosts();

   Post getPostById(Long id);
}