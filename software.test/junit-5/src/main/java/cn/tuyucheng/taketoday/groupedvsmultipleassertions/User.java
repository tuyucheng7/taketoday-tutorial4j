package cn.tuyucheng.taketoday.groupedvsmultipleassertions;

public class User {
   private final String username;
   private final String email;
   private final boolean activated;

   public User(String username, String email, boolean activated) {
      this.username = username;
      this.email = email;
      this.activated = activated;
   }

   public String getUsername() {
      return username;
   }

   public String getEmail() {
      return email;
   }

   public boolean getActivated() {
      return activated;
   }
}