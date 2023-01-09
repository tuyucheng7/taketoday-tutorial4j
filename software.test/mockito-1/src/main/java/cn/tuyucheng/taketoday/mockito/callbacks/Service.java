package cn.tuyucheng.taketoday.mockito.callbacks;

public interface Service {

	void doAction(String request, Callback<Response> callback);

}
