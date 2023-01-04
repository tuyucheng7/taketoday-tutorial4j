package cn.tuyucheng.taketoday.client;

import cn.tuyucheng.taketoday.resttemplate.web.dto.Foo;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

// This test needs RestTemplateConfigurationApplication to be up and running
class TestRestTemplateBasicLiveTest {

    private RestTemplate restTemplate;

    private static final String FOO_RESOURCE_URL = "http://localhost:" + 8082 + "/spring-rest/foos";
    private static final String URL_SECURED_BY_AUTHENTICATION = "http://httpbin.org/basic-auth/user/passwd";
    private static final String BASE_URL = "http://localhost:" + 8082 + "/spring-rest";

    @BeforeEach
    void beforeTest() {
        restTemplate = new RestTemplate();
    }

    // GET
    @Test
    void givenTestRestTemplate_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<Foo> response = testRestTemplate.getForEntity(FOO_RESOURCE_URL + "/1", Foo.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenRestTemplateWrapper_whenSendGetForEntity_thenStatusOk() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.configure(restTemplate);
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder);
        ResponseEntity<Foo> response = testRestTemplate.getForEntity(FOO_RESOURCE_URL + "/1", Foo.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenRestTemplateBuilderWrapper_whenSendGetForEntity_thenStatusOk() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.build();
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder);
        ResponseEntity<Foo> response = testRestTemplate.getForEntity(FOO_RESOURCE_URL + "/1", Foo.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenRestTemplateWrapperWithCredentials_whenSendGetForEntity_thenStatusOk() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.configure(restTemplate);
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder, "user", "passwd");
        ResponseEntity<String> response = testRestTemplate.getForEntity(URL_SECURED_BY_AUTHENTICATION,
              String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenTestRestTemplateWithCredentials_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
        ResponseEntity<String> response = testRestTemplate.getForEntity(URL_SECURED_BY_AUTHENTICATION,
              String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenTestRestTemplateWithBasicAuth_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> response = testRestTemplate.withBasicAuth("user", "passwd").
              getForEntity(URL_SECURED_BY_AUTHENTICATION, String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenTestRestTemplateWithCredentialsAndEnabledCookies_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd", TestRestTemplate.
              HttpClientOption.ENABLE_COOKIES);
        ResponseEntity<String> response = testRestTemplate.getForEntity(URL_SECURED_BY_AUTHENTICATION,
              String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    // HEAD
    @Test
    void givenFooService_whenCallHeadForHeaders_thenReceiveAllHeaders() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        final HttpHeaders httpHeaders = testRestTemplate.headForHeaders(FOO_RESOURCE_URL);
        Assertions.assertTrue(httpHeaders.getContentType().includes(MediaType.APPLICATION_JSON));
    }

    // POST
    @Test
    void givenService_whenPostForObject_thenCreatedObjectIsReturned() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
        final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("text/html; charset=utf-8"),
              "{\"id\":1,\"name\":\"Jim\"}");
        final Request request = new Request.Builder().url(BASE_URL + "/users/detail").post(body).build();
        testRestTemplate.postForObject(URL_SECURED_BY_AUTHENTICATION, request, String.class);
    }

    // PUT
    @Test
    void givenService_whenPutForObject_thenCreatedObjectIsReturned() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
        final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("text/html; charset=utf-8"),
              "{\"id\":1,\"name\":\"Jim\"}");
        final Request request = new Request.Builder().url(BASE_URL + "/users/detail").post(body).build();
        testRestTemplate.put(URL_SECURED_BY_AUTHENTICATION, request, String.class);
    }
}