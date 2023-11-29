package cn.tuyucheng.taketoday.object.type;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TreeSorterAssertJUnitTest {
   private final TreeSorter tested = new TreeSorter();

   @Test
   void sortTreeShouldReturnEvergreen_WhenPineIsPassed() {
      Tree tree = tested.sortTree("Pine");
      assertThat(tree).isExactlyInstanceOf(Evergreen.class);
   }

   @Test
   void sortTreeShouldReturnDecidious_WhenBirchIsPassed() {
      Tree tree = tested.sortTree("Birch");
      assertThat(tree).hasSameClassAs(new Deciduous("Birch"));
   }
}