package cn.tuyucheng.taketoday.controller;

import cn.tuyucheng.taketoday.domain.Approval;
import cn.tuyucheng.taketoday.domain.Article;
import cn.tuyucheng.taketoday.service.ArticleWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleWorkflowController {

   @Autowired
   private ArticleWorkflowService service;

   @PostMapping("/submit")
   public void submit(@RequestBody Article article) {
      service.startProcess(article);
   }

   @GetMapping("/tasks")
   public List<Article> getTasks(@RequestParam String assignee) {
      return service.getTasks(assignee);
   }

   @PostMapping("/review")
   public void review(@RequestBody Approval approval) {
      service.submitReview(approval);
   }
}