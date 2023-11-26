package cn.tuyucheng.taketoday.recordswithjpa.embeddable;

import jakarta.persistence.*;
import org.hibernate.annotations.EmbeddableInstantiator;

@Entity
@Table(name = "embeadable_author_book")
public class EmbeddableBook {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String title;
   @Embedded
   @EmbeddableInstantiator(AuthorInstallator.class)
   private Author author;
   private String isbn;

   public EmbeddableBook() {
   }

   public EmbeddableBook(Long id, String title, Author author, String isbn) {
      this.id = id;
      this.title = title;
      this.author = author;
      this.isbn = isbn;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Author getAuthor() {
      return author;
   }

   public void setAuthor(Author author) {
      this.author = author;
   }

   public String getIsbn() {
      return isbn;
   }

   public void setIsbn(String isbn) {
      this.isbn = isbn;
   }
}