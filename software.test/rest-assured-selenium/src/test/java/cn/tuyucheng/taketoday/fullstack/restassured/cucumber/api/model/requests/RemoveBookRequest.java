package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests;

public class RemoveBookRequest {
	public String isbn;
	public String userId;

	public RemoveBookRequest(String userId, String isbn) {
		this.userId = userId;
		this.isbn = isbn;
	}
}