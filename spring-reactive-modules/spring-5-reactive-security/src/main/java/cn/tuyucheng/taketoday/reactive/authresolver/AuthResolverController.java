package cn.tuyucheng.taketoday.reactive.authresolver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
public class AuthResolverController {

	@GetMapping("/customer/welcome")
	public Mono<String> sayWelcomeToCustomer(Mono<Principal> principal) {
		return principal
			.map(Principal::getName)
			.map(name -> String.format("Welcome to our site, %s!", name));
	}

	@GetMapping("/employee/welcome")
	public Mono<String> sayWelcomeToEmployee(Mono<Principal> principal) {
		return principal
			.map(Principal::getName)
			.map(name -> String.format("Welcome to our company, %s!", name));
	}
}