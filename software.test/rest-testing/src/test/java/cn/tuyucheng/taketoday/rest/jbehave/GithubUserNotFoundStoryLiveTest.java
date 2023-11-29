package cn.tuyucheng.taketoday.rest.jbehave;

public class GithubUserNotFoundStoryLiveTest extends AbstractStory {

   @Override
   String storyName() {
      return "github_user_not_found.story";
   }

   @Override
   Object stepInstance() {
      return new GithubUserNotFoundSteps();
   }
}