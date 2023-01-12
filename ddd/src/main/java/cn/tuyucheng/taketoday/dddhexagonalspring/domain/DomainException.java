package cn.tuyucheng.taketoday.dddhexagonalspring.domain;

class DomainException extends RuntimeException {
	DomainException(final String message) {
		super(message);
	}
}
