package cn.tuyucheng.taketoday.dirtiescontext;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserCache {

   @Getter
   private final Set<String> userList = new HashSet<>();

   public boolean addUser(String user) {
      return userList.add(user);
   }

   public boolean removeUser(String user) {
      return userList.remove(user);
   }

   public void printUserList(String message) {
      System.out.println(message + ": " + userList);
   }
}