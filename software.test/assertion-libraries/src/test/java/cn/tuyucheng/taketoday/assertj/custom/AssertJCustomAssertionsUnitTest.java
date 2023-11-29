package cn.tuyucheng.taketoday.assertj.custom;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static cn.tuyucheng.taketoday.assertj.custom.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AssertJCustomAssertionsUnitTest {
   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Test
   public void whenPersonNameMatches_thenCorrect() {
      Person person = new Person("John Doe", 20);
      assertThat(person).hasFullName("John Doe");
   }

   @Test
   public void whenPersonAgeLessThanEighteen_thenNotAdult() {
      Person person = new Person("Jane Roe", 16);

      try {
         assertThat(person).isAdult();
         fail();
      } catch (AssertionError e) {
         org.assertj.core.api.Assertions.assertThat(e).hasMessage("Expected person to be adult");
      }
   }

   @Test
   public void whenPersonDoesNotHaveAMatchingNickname_thenIncorrect() {
      Person person = new Person("John Doe", 20);
      person.addNickname("Nick");

      try {
         assertThat(person).hasNickname("John");
         fail();
      } catch (AssertionError e) {
         org.assertj.core.api.Assertions.assertThat(e).hasMessage("Expected person to have nickname John");
      }
   }
}