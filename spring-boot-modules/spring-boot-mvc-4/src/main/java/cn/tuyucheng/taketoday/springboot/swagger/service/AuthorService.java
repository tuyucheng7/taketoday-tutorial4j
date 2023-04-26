package cn.tuyucheng.taketoday.springboot.swagger.service;

import cn.tuyucheng.taketoday.springboot.swagger.model.Author;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {
	private List<Author> authors = new ArrayList<>();

	public List<Author> getAllAuthors() {
		return authors;
	}

	public void addAuthors(Author author) {
		author.setId(authors.size() + 1);
		authors.add(author);
	}
}