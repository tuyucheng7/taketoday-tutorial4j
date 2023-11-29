package cn.tuyucheng.taketoday.rest.jbehave;

public class GithubUserResponseMediaTypeStoryLiveTest extends AbstractStory {

   @Override
   String storyName() {
      return "github_user_response_mediatype.story";
   }

   @Override
   Object stepInstance() {
      return new GithubUserResponseMediaTypeSteps();
   }
}