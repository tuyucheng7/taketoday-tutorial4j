package cn.tuyucheng.taketoday.webservice;

import cn.tuyucheng.taketoday.webservice.generated.GetProductRequest;
import cn.tuyucheng.taketoday.webservice.generated.GetProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ProductEndpoint {

	private static final String NAMESPACE_URI = "http://tuyucheng.com/spring-boot-web-service";

	@Autowired
	private ProductRepository productRepository;


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProductRequest")
	@ResponsePayload
	public GetProductResponse getProduct(@RequestPayload GetProductRequest request) {
		GetProductResponse response = new GetProductResponse();
		response.setProduct(productRepository.findProduct(request.getId()));
		return response;
	}
}