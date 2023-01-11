package cn.tuyucheng.taketoday.roles.web;

import cn.tuyucheng.taketoday.roles.custom.persistence.model.Foo;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// In order to execute these tests, cn.tuyucheng.taketoday.roles.custom.CustomSecurityExpressionApplication needs to be running.
class CustomSecurityExpressionApplicationLiveTest {

    @Test
    void givenUserWithReadPrivilegeAndHasPermission_whenGetFooById_thenOK() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/foos/1");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    void givenUserWithNoWritePrivilegeAndHasPermission_whenPostFoo_thenForbidden() {
        final Response response = givenAuth("john", "123").contentType(MediaType.APPLICATION_JSON_VALUE).body(new Foo("sample")).post("http://localhost:8082/foos");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void givenUserWithWritePrivilegeAndHasPermission_whenPostFoo_thenOk() {
        final Response response = givenAuth("tom", "111").and().body(new Foo("sample")).and().contentType(MediaType.APPLICATION_JSON_VALUE).post("http://localhost:8082/foos");
        assertEquals(201, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    void givenUserMemberInOrganization_whenGetOrganization_thenOK() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/organizations/1");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    void givenUserMemberNotInOrganization_whenGetOrganization_thenForbidden() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/organizations/2");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void givenDisabledSecurityExpression_whenGetFooByName_thenError() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/foos?name=sample");
        assertEquals(500, response.getStatusCode());
        // assertTrue(response.asString().contains("method hasAuthority() not allowed"));
    }

    private RequestSpecification givenAuth(String username, String password) {
        return given().log().uri().auth().form(username, password, new FormAuthConfig("/login", "username", "password"));
    }
}