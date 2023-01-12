package cn.tuyucheng.taketoday.feign.clients.header.staticheader.parameterized;

import cn.tuyucheng.taketoday.feign.models.BookResource;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


@Headers("x-requester-id: {requester}")
public interface BookClient {

	@RequestLine("GET /{isbn}")
	BookResource findByIsbn(@Param("requester") String requestorId, @Param("isbn") String isbn);

}
