package cn.tuyucheng.taketoday.validations.functional.handlers.impl;

import cn.tuyucheng.taketoday.validations.functional.handlers.AbstractValidationHandler;
import cn.tuyucheng.taketoday.validations.functional.model.CustomRequestEntity;
import cn.tuyucheng.taketoday.validations.functional.validators.CustomRequestEntityValidator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CustomRequestEntityValidationHandler extends AbstractValidationHandler<CustomRequestEntity, CustomRequestEntityValidator> {

	private CustomRequestEntityValidationHandler() {
		super(CustomRequestEntity.class, new CustomRequestEntityValidator());
	}

	@Override
	protected Mono<ServerResponse> processBody(CustomRequestEntity validBody, ServerRequest originalRequest) {
		String responseBody = String.format("Hi, %s [%s]!", validBody.getName(), validBody.getCode());
		return ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(responseBody), String.class);
	}

	@Override
	protected Mono<ServerResponse> onValidationErrors(Errors errors, CustomRequestEntity invalidBody, final ServerRequest request) {
		return ServerResponse.badRequest()
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(String.format("Custom message showing the errors: %s", errors.getAllErrors())), String.class);
	}
}