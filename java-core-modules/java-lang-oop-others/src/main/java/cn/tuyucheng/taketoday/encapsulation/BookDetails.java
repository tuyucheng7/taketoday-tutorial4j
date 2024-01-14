package cn.tuyucheng.taketoday.encapsulation;

public class BookDetails {

   public String bookDetails(Book book) {
      return "author name: " + book.author + " ISBN: " + book.isbn;
   }
}
