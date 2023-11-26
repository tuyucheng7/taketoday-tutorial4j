package cn.tuyucheng.taketoday.resource;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Before running this live test make sure both authorization server and resource server in this module are running

public class AuthorizationCodeLiveTest {
   public final static String AUTH_SERVER = "http://localhost:8083/auth/realms/tuyucheng/protocol/openid-connect";
   public final static String RESOURCE_SERVER = "http://localhost:8081/sso-resource-server";
   private final static String REDIRECT_URL = "http://localhost:8082/ui-one/login/oauth2/code/custom";
   private final static String CLIENT_ID = "ssoClient-1";
   private final static String CLIENT_SECRET = "ssoClientSecret-1";

   @Test
   public void givenUser_whenUseFooClient_thenOkForFooResourceOnly() {
      final String accessToken = obtainAccessTokenWithAuthorizationCode("john@test.com", "123");

      final Response fooResponse = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get(RESOURCE_SERVER + "/api/foos/1");
      assertEquals(200, fooResponse.getStatusCode());
      assertNotNull(fooResponse.jsonPath()
            .get("name"));

   }

   private String obtainAccessTokenWithAuthorizationCode(String username, String password) {

      String authorizeUrl = AUTH_SERVER + "/auth";
      String tokenUrl = AUTH_SERVER + "/token";

      Map<String, String> loginParams = new HashMap<String, String>();
      loginParams.put("client_id", CLIENT_ID);
      loginParams.put("response_type", "code");
      loginParams.put("redirect_uri", REDIRECT_URL);
      loginParams.put("scope", "read write");

      // user login
      Response response = RestAssured.given()
            .formParams(loginParams)
            .get(authorizeUrl);
      String cookieValue = response.getCookie("AUTH_SESSION_ID");

      String authUrlWithCode = response.htmlPath()
            .getString("'**'.find{node -> node.name()=='form'}*.@action");

      // get code
      Map<String, String> codeParams = new HashMap<String, String>();
      codeParams.put("username", username);
      codeParams.put("password", password);
      response = RestAssured.given()
            .cookie("AUTH_SESSION_ID", cookieValue)
            .formParams(codeParams)
            .post(authUrlWithCode);

      final String location = response.getHeader(HttpHeaders.LOCATION);

      assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
      final String code = location.split("#|=|&")[3];

      // get access token
      Map<String, String> tokenParams = new HashMap<String, String>();
      tokenParams.put("grant_type", "authorization_code");
      tokenParams.put("client_id", CLIENT_ID);
      tokenParams.put("client_secret", CLIENT_SECRET);
      tokenParams.put("redirect_uri", REDIRECT_URL);
      tokenParams.put("code", code);

      response = RestAssured.given()
            .formParams(tokenParams)
            .post(tokenUrl);

      return response.jsonPath()
            .getString("access_token");
   }

}
