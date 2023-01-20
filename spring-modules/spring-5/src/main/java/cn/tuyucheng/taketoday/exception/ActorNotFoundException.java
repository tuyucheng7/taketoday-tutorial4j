package cn.tuyucheng.taketoday.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Actor Not Found")
public class ActorNotFoundException extends Exception {
	@Serial
	private static final long serialVersionUID = 1L;

	public ActorNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}