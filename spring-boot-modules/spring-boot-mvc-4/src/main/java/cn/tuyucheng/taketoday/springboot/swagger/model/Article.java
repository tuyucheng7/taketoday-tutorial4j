package cn.tuyucheng.taketoday.springboot.swagger.model;

import io.swagger.v3.oas.annotations.Hidden;

public class Article {

   // @JsonIgnore
   // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
   // @Schema(accessMode = AccessMode.READ_ONLY)
   @Hidden
   private int id;
   private String title;
   private int numOfWords;

   public Article() {
   }

   public Article(int id, String title) {
      this.id = id;
      this.title = title;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public int getNumOfWords() {
      return numOfWords;
   }

   public void setNumOfWords(int numOfWords) {
      this.numOfWords = numOfWords;
   }
}