package cn.tuyucheng.taketoday.boot.json.convertfile.service;

import cn.tuyucheng.taketoday.boot.json.convertfile.ImportUtils;
import cn.tuyucheng.taketoday.boot.json.convertfile.dao.BookRepository;
import cn.tuyucheng.taketoday.boot.json.convertfile.data.Book;
import com.mongodb.DBObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@ExtendWith(SpringExtension.class)
class ImportJsonServiceIntegrationTest {
	@Autowired
	private ImportJsonService service;

	@Autowired
	private MongoTemplate mongoDb;

	@Autowired
	BookRepository bookRepository;

	@Test
	void givenJsonString_whenGenericType_thenDocumentImported() {
		String collection = "items";
		List<DBObject> docs = mongoDb.findAll(DBObject.class, collection);
		int sizeBefore = docs.size();

		String json = "{\"name\":\"Item A\"}\n{\"name\":\"Item B\"}";
		List<String> jsonLines = ImportUtils.lines(json);
		service.importTo(collection, jsonLines);

		docs = mongoDb.findAll(DBObject.class, collection);
		int sizeAfter = docs.size();
		assertThat(sizeAfter - sizeBefore).isEqualTo(jsonLines.size());
	}

	@Test
	void givenJsonFile_whenClasspathSource_thenDocumentImported() {
		String collection = "movies";
		List<DBObject> docs = mongoDb.findAll(DBObject.class, collection);
		int sizeBefore = docs.size();

		String resource = "boot.json.convertfile/movies.json.log";
		List<String> jsonLines = ImportUtils.linesFromResource(resource);
		service.importTo(collection, jsonLines);

		docs = mongoDb.findAll(DBObject.class, collection);
		int sizeAfter = docs.size();
		assertThat(sizeAfter - sizeBefore).isEqualTo(jsonLines.size());
	}

	@Test
	void givenJsonClasspathFile_whenCorrectlyTyped_thenDocumentImported() {
		List<Book> books = bookRepository.findAll();
		int sizeBefore = books.size();

		String resource = "boot.json.convertfile/books.json.log";
		List<String> jsonLines = ImportUtils.linesFromResource(resource);
		service.importTo(Book.class, jsonLines);

		books = bookRepository.findAll();
		int sizeAfter = books.size();
		assertThat(sizeAfter - sizeBefore).isEqualTo(jsonLines.size());
	}

	@Test
	void givenIncorrectlyTypedJson_whenUsingTypes_thenDocumentNotImported() {
		List<Book> books = bookRepository.findAll();
		int sizeBefore = books.size();

		String resource = "boot.json.convertfile/movies.json.log";
		List<String> jsonLines = ImportUtils.linesFromResource(resource);
		service.importTo(Book.class, jsonLines);

		books = bookRepository.findAll();
		int sizeAfter = books.size();
		assertThat(sizeAfter - sizeBefore).isEqualTo(0);
	}

	@Test
	void whenInvalidJson_thenDocumentNotImported() {
		String collection = "items";
		List<DBObject> docs = mongoDb.findAll(DBObject.class, collection);
		int sizeBefore = docs.size();

		String json = "{name: Item A}\n{name: Item B}";
		List<String> jsonLines = ImportUtils.lines(json);
		service.importTo(collection, jsonLines);

		docs = mongoDb.findAll(DBObject.class, collection);
		int sizeAfter = docs.size();
		assertThat(sizeAfter - sizeBefore).isEqualTo(0);
	}
}