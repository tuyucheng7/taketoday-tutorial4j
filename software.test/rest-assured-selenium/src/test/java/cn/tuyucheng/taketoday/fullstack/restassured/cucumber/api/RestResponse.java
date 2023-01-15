package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api;

import io.restassured.response.Response;

public interface RestResponse<T> {

	T getBody();

	String getContent();

	int getStatusCode();

	boolean isSuccessful();

	String getStatusDescription();

	Response getResponse();

	Exception getException();
}