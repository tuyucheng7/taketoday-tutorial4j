package cn.tuyucheng.taketoday.snippettag;

/**
 * Using the replace tag
 * {@snippet :
 * public void helloTuyucheng() {
 *     System.out.println("Hello From Team Tuyucheng"); // @replace regex='".*"' replacement="..."
 * }
 *}
 * Using the link tag
 * {@snippet :
 * public void helloTuyucheng() {
 *     System.out.println("Hello From Team Tuyucheng"); // @link substring="System.out" target="System#out"
 * }
 *}
 */
public class GreetingsReplaceAndLinkTag {

   public void helloTuyucheng() {
      System.out.println("Hello From Team Tuyucheng");
   }
}