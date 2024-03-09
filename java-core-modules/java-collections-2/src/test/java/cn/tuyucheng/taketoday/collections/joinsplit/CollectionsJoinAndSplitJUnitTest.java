package cn.tuyucheng.taketoday.collections.joinsplit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CollectionsJoinAndSplitJUnitTest {

   private ArrayList<String> sauces = new ArrayList<>();
   private ArrayList<String> cheeses = new ArrayList<>();
   private ArrayList<String> vegetables = new ArrayList<>();

   private ArrayList<ArrayList<String>> ingredients = new ArrayList<>();

   @BeforeEach
   public void init() {
      sauces.add("Olive Oil");
      sauces.add("Marinara");

      cheeses.add("Mozzarella");
      cheeses.add("Feta");
      cheeses.add("Parmesan");

      vegetables.add("Olives");
      vegetables.add("Spinach");
      vegetables.add("Green Peppers");

      ingredients.add(sauces);
      ingredients.add(cheeses);
      ingredients.add(vegetables);
   }

   @Test
   public void givenThreeArrayLists_whenJoiningIntoOneArrayList_shouldSucceed() {
      ArrayList<ArrayList<String>> toppings = new ArrayList<>();

      toppings.add(sauces);
      toppings.add(cheeses);
      toppings.add(vegetables);

      Assertions.assertTrue(toppings.size() == 3);
      Assertions.assertTrue(toppings.contains(sauces));
      Assertions.assertTrue(toppings.contains(cheeses));
      Assertions.assertTrue(toppings.contains(vegetables));
   }

   @Test
   public void givenOneArrayList_whenSplittingIntoTwoArrayLists_shouldSucceed() {

      ArrayList<ArrayList<String>> removedToppings = new ArrayList<>();
      removedToppings.add(ingredients.remove(ingredients.indexOf(vegetables)));

      Assertions.assertTrue(removedToppings.contains(vegetables));
      Assertions.assertTrue(removedToppings.size() == 1);
      Assertions.assertTrue(ingredients.size() == 2);
      Assertions.assertTrue(ingredients.contains(sauces));
      Assertions.assertTrue(ingredients.contains(cheeses));
   }
}
