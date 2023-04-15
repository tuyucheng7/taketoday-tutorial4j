package cn.tuyucheng.taketoday.boot.grpc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrpcClientController {

	@Autowired
	private GrpcClientService grpcClientService;

	@RequestMapping("/")
	public String printMessage(@RequestParam(defaultValue = "Michael") String name) {
		return grpcClientService.sendMessage(name);
	}
}