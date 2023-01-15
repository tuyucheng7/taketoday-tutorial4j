package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api;

import io.restassured.response.Response;

public class GeneralRestResponse<T> implements RestResponse<T> {

	private T data;
	private Response response;
	private Exception e;

	public GeneralRestResponse(Class<T> t, Response response) {
		this.response = response;
		try {
			this.data = t.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("There should be a default constructor in the Response POJO");
		}
	}

	public String getContent() {
		return response.getBody().asString();
	}

	public int getStatusCode() {
		return response.getStatusCode();
	}

	public boolean isSuccessful() {
		int code = response.getStatusCode();
		return code == 200 || code == 201 || code == 202 || code == 203 || code == 204 || code == 205;
	}

	public String getStatusDescription() {
		return response.getStatusLine();
	}

	public Response getResponse() {
		return response;
	}


	public T getBody() {
		try {
			data = (T) response.getBody().as(data.getClass());
		} catch (Exception e) {
			this.e = e;
		}
		return data;
	}

	public Exception getException() {
		return e;
	}
}