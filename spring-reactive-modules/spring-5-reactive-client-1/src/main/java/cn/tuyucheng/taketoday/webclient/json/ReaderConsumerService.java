package cn.tuyucheng.taketoday.webclient.json;

import cn.tuyucheng.taketoday.webclient.json.model.Book;

import java.util.List;

public interface ReaderConsumerService {

	List<Book> processReaderDataFromObjectArray();

	List<Book> processReaderDataFromReaderArray();

	List<Book> processReaderDataFromReaderList();

	List<String> processNestedReaderDataFromReaderArray();

	List<String> processNestedReaderDataFromReaderList();
}