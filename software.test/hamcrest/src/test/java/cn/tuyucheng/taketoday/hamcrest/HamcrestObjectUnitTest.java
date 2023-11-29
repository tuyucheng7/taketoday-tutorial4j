package cn.tuyucheng.taketoday.hamcrest;

import cn.tuyucheng.taketoday.hamcrest.objectmatchers.City;
import cn.tuyucheng.taketoday.hamcrest.objectmatchers.Location;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class HamcrestObjectUnitTest {

   @Test
   void givenACity_whenHasToString_thenCorrect() {
      City city = new City("San Francisco", "CA");

      assertThat(city, hasToString("[Name: San Francisco, State: CA]"));
   }

   @Test
   void givenACity_whenHasToStringEqualToIgnoringCase_thenCorrect() {
      City city = new City("San Francisco", "CA");

      assertThat(city, hasToString(equalToIgnoringCase("[NAME: SAN FRANCISCO, STATE: CA]")));
   }

   @Test
   void givenACity_whenHasToStringEmptyOrNullString_thenCorrect() {
      City city = new City(null, null);

      assertThat(city, hasToString(emptyOrNullString()));
   }

   @Test
   void givenACity_whenTypeCompatibleWithLocation_thenCorrect() {
      City city = new City("San Francisco", "CA");

      assertThat(city.getClass(), is(typeCompatibleWith(Location.class)));
   }

   @Test
   void givenACity_whenTypeNotCompatibleWithString_thenCorrect() {
      City city = new City("San Francisco", "CA");

      assertThat(city.getClass(), is(not(typeCompatibleWith(String.class))));
   }

   @Test
   void givenACity_whenTypeCompatibleWithObject_thenCorrect() {
      City city = new City("San Francisco", "CA");

      assertThat(city.getClass(), is(typeCompatibleWith(Object.class)));
   }
}