package cn.tuyucheng.taketoday.springmultipledatasources.topics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}