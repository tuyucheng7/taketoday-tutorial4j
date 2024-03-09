package cn.tuyucheng.taketoday.listvsset.lazy.list.moderatedomain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}