package cn.tuyucheng.taketoday.springdoc.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tuyucheng.taketoday.springdoc.demo.model.Topic;
import cn.tuyucheng.taketoday.springdoc.demo.service.TopicService;

@RestController
public class TopicsController {

	@Autowired
	TopicService topicService;

	@GetMapping(value = "/topics")
	public ResponseEntity<List<Topic>> getAllTopics() {
		return new ResponseEntity<>(topicService.getAlllTopics(), HttpStatus.OK);
	}
}