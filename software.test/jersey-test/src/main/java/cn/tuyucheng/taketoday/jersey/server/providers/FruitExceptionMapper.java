package cn.tuyucheng.taketoday.jersey.server.providers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class FruitExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(final ConstraintViolationException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
			.entity(prepareMessage(exception))
			.type("text/plain")
			.build();
	}

	private String prepareMessage(ConstraintViolationException exception) {
		final StringBuilder message = new StringBuilder();
		for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
			message.append(cv.getPropertyPath())
				.append(" ")
				.append(cv.getMessage())
				.append("\n");
		}
		return message.toString();
	}
}