package cn.tuyucheng.taketoday.roles.web;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// In order to execute these tests, cn.tuyucheng.taketoday.roles.ip.IpApplication needs to be running.
class IpLiveTest {

    @Test
    void givenUser_whenGetHomePage_thenOK() {
        final Response response = RestAssured.given().auth().form("john", "123").get("http://localhost:8082/");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Welcome"));
    }

    @Test
    void givenUserWithWrongIP_whenGetFooById_thenForbidden() {
        final Response response = RestAssured.given().auth().form("john", "123").get("http://localhost:8082/foos/1");
        assertEquals(403, response.getStatusCode());
        assertTrue(response.asString().contains("Forbidden"));
    }
}