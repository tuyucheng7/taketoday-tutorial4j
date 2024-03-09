package cn.tuyucheng.taketoday.inheritancecomposition;

import cn.tuyucheng.taketoday.inheritancecomposition.model.Actress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ActressUnitTest {

   private static Actress actress;

   @BeforeAll
   public static void setUpActressInstance() {
      actress = new Actress("Susan", "susan@domain.com", 30);
   }

   @Test
   public void givenActressInstance_whenCalledgetName_thenEqual() {
      assertThat(actress.getName()).isEqualTo("Susan");
   }

   @Test
   public void givenActressInstance_whenCalledgetEmail_thenEqual() {
      assertThat(actress.getEmail()).isEqualTo("susan@domain.com");
   }

   @Test
   public void givenActressInstance_whenCalledgetAge_thenEqual() {
      assertThat(actress.getAge()).isEqualTo(30);
   }

   @Test
   public void givenActressInstance_whenCalledreadScript_thenEqual() {
      assertThat(actress.readScript("Psycho")).isEqualTo("Reading the script of Psycho");
   }

   @Test
   public void givenActressInstance_whenCalledperfomRole_thenEqual() {
      assertThat(actress.performRole()).isEqualTo("Performing a role");
   }
}
