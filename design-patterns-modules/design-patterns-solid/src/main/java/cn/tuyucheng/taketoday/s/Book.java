package cn.tuyucheng.taketoday.s;

public class Book {

	private String name;
	private String author;
	private String text;

	public Book(String name, String author, String text) {
		this.name = name;
		this.author = author;
		this.text = text;
	}

	// methods that directly relate to the book properties
	public String replaceWordInText(String word, String replacementWord) {
		return text.replaceAll(word, replacementWord);
	}

	public boolean isWordInText(String word) {
		return text.contains(word);
	}
}