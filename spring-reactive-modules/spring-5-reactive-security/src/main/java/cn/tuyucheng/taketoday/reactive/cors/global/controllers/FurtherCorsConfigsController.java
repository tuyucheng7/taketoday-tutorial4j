package cn.tuyucheng.taketoday.reactive.cors.global.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController("FurtherCorsConfigsController-cors-on-global-config-and-more")
@RequestMapping("/cors-on-global-config-and-more")
public class FurtherCorsConfigsController {

	@DeleteMapping("/further-mixed-config-endpoint")
	@CrossOrigin(methods = {RequestMethod.DELETE},
		allowedHeaders = {"Tuyucheng-Other-Allowed"},
		exposedHeaders = {"Tuyucheng-Other-Exposed"}
	)
	public Mono<String> corsEnabledHeaderExposedEndpoint() {
		return Mono.just("CORS Global Configured + Request Configs.");
	}
}