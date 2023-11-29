package cn.tuyucheng.taketoday.junit5.nested;

public class Article {
   private String name;
   private Membership articleLevel;

   public Article(String name, Membership articleLevel) {
      this.name = name;
      this.articleLevel = articleLevel;
   }

   public String getName() {
      return name;
   }

   public Membership getArticleLevel() {
      return articleLevel;
   }
}