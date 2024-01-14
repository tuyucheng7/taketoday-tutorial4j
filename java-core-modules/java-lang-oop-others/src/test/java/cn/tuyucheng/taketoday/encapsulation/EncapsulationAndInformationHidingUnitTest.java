package cn.tuyucheng.taketoday.encapsulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncapsulationAndInformationHidingUnitTest {

   @Test
   public void givenUnencapsulatedClass_whenImplementationClassIsSeparate_thenReturnResult() {
      Book myBook = new Book();
      myBook.author = "J.K Rowlings";
      myBook.isbn = 67890;
      BookDetails details = new BookDetails();
      String result = details.bookDetails(myBook);
      assertEquals("author name: " + myBook.author + " ISBN: " + myBook.isbn, result);

   }

   @Test
   public void givenEncapsulatedClass_whenDataIsNotHidden_thenReturnResult() {
      BookEncapsulation myBook = new BookEncapsulation("J.K Rowlings", 67890);
      String result = myBook.getBookDetails();
      assertEquals("author id: " + 1 + " author name: " + myBook.author + " ISBN: " + myBook.isbn, result);
   }

   @Test
   public void givenEncapsulatedClass_whenDataIsHidden_thenReturnResult() {
      BookInformationHiding myBook = new BookInformationHiding("J.K Rowlings", 67890);
      String result = myBook.getBookDetails();
      assertEquals("author id: " + 1 + " author name: " + myBook.getAuthor() + " ISBN: " + myBook.getIsbn(), result);
   }

}
