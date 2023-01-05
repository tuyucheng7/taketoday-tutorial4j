package cn.tuyucheng.taketoday.vavr.exception.handling.client;

public interface HttpClient {
	Response call() throws ClientException;
}