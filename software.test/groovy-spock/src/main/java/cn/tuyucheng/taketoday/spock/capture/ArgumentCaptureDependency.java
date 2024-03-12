package cn.tuyucheng.taketoday.spock.capture;

public class ArgumentCaptureDependency {

   public String catchMe(final String input) {
      return "***" + input + "***";
   }
}