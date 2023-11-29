package cn.tuyucheng.taketoday.assertj;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

class AssertJConditionUnitTest {
   private Condition<Member> senior = new Condition<>(m -> m.getAge() >= 60, "senior");
   private Condition<Member> nameJohn = new Condition<>(m -> m.getName().equalsIgnoreCase("John"), "name John");

   @Test
   void whenUsingMemberAgeCondition_thenCorrect() {
      Member member = new Member("John", 65);
      assertThat(member).is(senior);

      try {
         assertThat(member).isNot(senior);
         fail();
      } catch (AssertionError e) {
         assertThat(e).hasMessageContaining("not to be senior");
      }
   }

   @Test
   void whenUsingMemberNameCondition_thenCorrect() {
      Member member = new Member("Jane", 60);
      assertThat(member).doesNotHave(nameJohn);

      try {
         assertThat(member).has(nameJohn);
         fail();
      } catch (AssertionError e) {
         assertThat(e).hasMessageContaining("name John");
      }
   }

   @Test
   void whenCollectionConditionsAreSatisfied_thenCorrect() {
      List<Member> members = new ArrayList<>();
      members.add(new Member("Alice", 50));
      members.add(new Member("Bob", 60));

      Assertions.assertThat(members).haveExactly(1, senior);
      Assertions.assertThat(members).doNotHave(nameJohn);
   }

   @Test
   void whenCombiningAllOfConditions_thenCorrect() {
      Member john = new Member("John", 60);
      Member jane = new Member("Jane", 50);

      assertThat(john).is(allOf(senior, nameJohn));
      assertThat(jane).is(allOf(Assertions.not(nameJohn), Assertions.not(senior)));
   }

   @Test
   void whenCombiningAnyOfConditions_thenCorrect() {
      Member john = new Member("John", 50);
      Member jane = new Member("Jane", 60);

      assertThat(john).is(anyOf(senior, nameJohn));
      assertThat(jane).is(anyOf(nameJohn, senior));
   }
}