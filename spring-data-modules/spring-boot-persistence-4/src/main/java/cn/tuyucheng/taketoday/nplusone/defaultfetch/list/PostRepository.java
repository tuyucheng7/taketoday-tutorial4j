package cn.tuyucheng.taketoday.nplusone.defaultfetch.list;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}