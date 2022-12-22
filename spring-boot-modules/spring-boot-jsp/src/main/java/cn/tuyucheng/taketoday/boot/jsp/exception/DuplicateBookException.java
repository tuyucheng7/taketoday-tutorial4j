package cn.tuyucheng.taketoday.boot.jsp.exception;

import cn.tuyucheng.taketoday.boot.jsp.dto.Book;
import lombok.Getter;

@Getter
public class DuplicateBookException extends RuntimeException {
	private final Book book;

	public DuplicateBookException(Book book) {
		this.book = book;
	}
}