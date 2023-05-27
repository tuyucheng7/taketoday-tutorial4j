package cn.tuyucheng.taketoday.resource.web.dto;

public class FooDto {
   private long id;
   private String name;

   public FooDto() {
      super();
   }

   public FooDto(final long id, final String name) {
      super();

      this.id = id;
      this.name = name;
   }

   public long getId() {
      return id;
   }

   public void setId(final long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(final String name) {
      this.name = name;
   }

}