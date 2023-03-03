package cn.tuyucheng.taketoday.swaggerjwt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController(value = "/clients")
@Api(tags = "Clients")
public class ClientsRestController {

	@ApiOperation(value = "This method is used to get the clients.")
	@GetMapping
	public List<String> getClients() {
		return Arrays.asList("First Client", "Second Client");
	}
}