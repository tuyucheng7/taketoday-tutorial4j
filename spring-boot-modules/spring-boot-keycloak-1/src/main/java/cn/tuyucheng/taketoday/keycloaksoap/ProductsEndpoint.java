package cn.tuyucheng.taketoday.keycloaksoap;

import cn.tuyucheng.taketoday.DeleteProductRequest;
import cn.tuyucheng.taketoday.DeleteProductResponse;
import cn.tuyucheng.taketoday.GetProductDetailsRequest;
import cn.tuyucheng.taketoday.GetProductDetailsResponse;
import cn.tuyucheng.taketoday.Product;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.annotation.security.RolesAllowed;
import java.util.Map;

@Endpoint
public class ProductsEndpoint {

	private final Map<String, Product> productMap;

	public ProductsEndpoint(Map<String, Product> productMap) {
		this.productMap = productMap;
	}

	@RolesAllowed("user")
	@PayloadRoot(namespace = "http://www.baeldung.com/springbootsoap/keycloak", localPart = "getProductDetailsRequest")
	@ResponsePayload
	public GetProductDetailsResponse getProductDetails(@RequestPayload GetProductDetailsRequest request) {
		GetProductDetailsResponse response = new GetProductDetailsResponse();
		response.setProduct(productMap.get(request.getId()));
		return response;
	}

	@RolesAllowed("admin")
	@PayloadRoot(namespace = "http://www.baeldung.com/springbootsoap/keycloak", localPart = "deleteProductRequest")
	@ResponsePayload
	public DeleteProductResponse deleteProduct(@RequestPayload DeleteProductRequest request) {
		DeleteProductResponse response = new DeleteProductResponse();
		response.setMessage("Success! Deleted the product with the id - " + request.getId());
		return response;
	}
}