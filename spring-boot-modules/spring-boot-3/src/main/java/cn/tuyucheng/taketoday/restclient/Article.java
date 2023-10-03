package cn.tuyucheng.taketoday.restclient;

import java.util.Objects;

public class Article {
   Integer id;
   String title;

   public Article(Integer id, String title) {
      this.id = id;
      this.title = title;
   }

   public Integer getId() {
      return id;
   }

   public String getTitle() {
      return title;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Article article = (Article) o;
      return Objects.equals(id, article.id) && Objects.equals(title, article.title);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, title);
   }
}