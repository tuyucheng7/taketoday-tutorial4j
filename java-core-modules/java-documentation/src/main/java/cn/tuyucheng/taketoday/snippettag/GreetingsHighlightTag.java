package cn.tuyucheng.taketoday.snippettag;


/**
 * The code below shows a full highlighted line
 * {@snippet :
 * public void helloTuyucheng() {
 *     System.out.println("Hello From Team Tuyucheng"); // @highlight
 * }
 *}
 * <p>
 * highlighting a substring
 * {@snippet :
 * public void helloTuyucheng() {
 *     System.out.println("Hello From Team Tuyucheng"); // @highlight substring="println"
 * }
 *}
 * <p>
 * highlighting texts on multiple lines
 * {@snippet :
 * public void helloTuyucheng() {
 *     System.out.println("Hello From Team Tuyucheng"); // @highlight region substring="println"
 *     String country = "USA";
 *     System.out.println("Hello From Team " + country); // @end
 * }
 *}
 */

public class GreetingsHighlightTag {
   public void helloTuyucheng() {
      System.out.println("Hello From Team Tuyucheng");
   }

}
