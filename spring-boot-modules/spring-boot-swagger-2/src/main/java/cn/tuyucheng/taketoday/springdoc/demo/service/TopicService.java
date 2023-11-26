package cn.tuyucheng.taketoday.springdoc.demo.service;

import cn.tuyucheng.taketoday.springdoc.demo.model.Topic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService {

   private List<Topic> topicsList;

   public TopicService() {
      this.topicsList = new ArrayList<Topic>() {{
         add(new Topic(1, "Topic1"));
         add(new Topic(2, "Topic2"));
         add(new Topic(3, "Topic3"));
      }};
   }

   public List<Topic> getAlllTopics() {
      return topicsList;
   }
}