package cn.tuyucheng.taketoday.micronautreactive.dtos;

public class BookNotFoundException extends Exception {
   public BookNotFoundException(String id) {
      super("Book with id " + id + " not found");
   }
}