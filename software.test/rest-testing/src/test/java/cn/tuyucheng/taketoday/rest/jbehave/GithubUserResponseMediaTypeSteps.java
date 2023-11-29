package cn.tuyucheng.taketoday.rest.jbehave;

import org.apache.http.entity.ContentType;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.io.IOException;

import static cn.tuyucheng.taketoday.rest.jbehave.GithubUserNotFoundSteps.getGithubUserProfile;
import static org.junit.Assert.assertEquals;

public class GithubUserResponseMediaTypeSteps {

   private String api;
   private String validUser;
   private String mediaType;

   @Given("github user profile api")
   public void givenGithubUserProfileApi() {
      api = "https://api.github.com/users/%s";
   }

   @Given("a valid username")
   public void givenAValidUsername() {
      validUser = "tuyucheng";
   }

   @When("I look for the user via the api")
   public void whenILookForTheUserViaTheApi() throws IOException {
      mediaType = ContentType
            .getOrDefault(getGithubUserProfile(api, validUser).getEntity())
            .getMimeType();
   }

   @Then("github respond data of type json")
   public void thenGithubRespondDataOfTypeJson() {
      assertEquals("application/json", mediaType);
   }
}