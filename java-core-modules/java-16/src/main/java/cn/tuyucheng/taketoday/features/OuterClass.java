package cn.tuyucheng.taketoday.features;

import cn.tuyucheng.taketoday.features.record.Book;

public class OuterClass {
	class InnerClass {
		Book book = new Book("Title", "author", "isbn");
	}
}