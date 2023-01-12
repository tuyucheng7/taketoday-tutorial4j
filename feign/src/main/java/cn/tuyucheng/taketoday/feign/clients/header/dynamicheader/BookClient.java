package cn.tuyucheng.taketoday.feign.clients.header.dynamicheader;

import cn.tuyucheng.taketoday.feign.models.Book;
import feign.HeaderMap;
import feign.Headers;
import feign.RequestLine;

import java.util.Map;

@Headers("Content-Type: application/json")
public interface BookClient {

	@RequestLine("POST")
	void create(@HeaderMap Map<String, Object> headers, Book book);

}
