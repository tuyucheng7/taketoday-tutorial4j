package cn.tuyucheng.taketoday.declarativehttpclient.web;

import cn.tuyucheng.taketoday.declarativehttpclient.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HttpExchange(url = "/users", accept = "application/json", contentType = "application/json")
public interface UserClient {

	@GetExchange("/")
	Flux<User> getAll();

	@GetExchange("/{id}")
	Mono<User> getById(@PathVariable("id") Long id);

	@PostExchange("/")
	Mono<ResponseEntity<Void>> save(@RequestBody User user);

	@PutExchange("/{id}")
	Mono<ResponseEntity<Void>> update(@PathVariable Long id, @RequestBody User user);

	@DeleteExchange("/{id}")
	Mono<ResponseEntity<Void>> delete(@PathVariable Long id);
}