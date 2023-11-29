package cn.tuyucheng.taketoday.truth;

import com.google.common.collect.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.tuyucheng.taketoday.truth.UserSubject.assertThat;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.Truth8.assertThat;

class GoogleTruthUnitTest {

   @Test
   void whenComparingInteger_thenEqual() {
      int anInt = 10;

      assertThat(anInt).isEqualTo(10);
   }

   @Test
   void whenComparingFloat_thenIsBigger() {
      float aFloat = 10.0f;

      assertThat(aFloat).isGreaterThan(1.0f);
   }

   @Test
   void whenComparingDouble_thenIsSmaller() {
      double aDouble = 10.0f;

      assertThat(aDouble).isLessThan(20.0);
   }

   @Test
   void whenComparingFloat_thenWithinPrecision() {
      float aFloat = 23.04f;

      assertThat(aFloat).isWithin(1.3f)
            .of(23.3f);
   }

   @Test
   void whenComparingFloat_thenNotWithinPrecision() {
      float aFloat = 23.04f;

      assertThat(aFloat).isNotWithin(1.3f)
            .of(100f);
   }

   @Test
   void whenComparingDouble_thenWithinPrecision() {
      double aDouble = 22.18;

      assertThat(aDouble).isWithin(2)
            .of(23d);
   }

   @Test
   void whenComparingDouble_thenNotWithinPrecision() {
      double aDouble = 22.08;

      assertThat(aDouble).isNotWithin(2)
            .of(100);
   }

   @Test
   void whenComparingBigDecimal_thenEqualIgnoringScale() {
      BigDecimal aBigDecimal = BigDecimal.valueOf(1000, 3);

      assertThat(aBigDecimal).isEqualToIgnoringScale(new BigDecimal(1.0));
   }

   @Test
   void whenCheckingBoolean_thenTrue() {
      boolean aBoolean = true;

      assertThat(aBoolean).isTrue();
   }

   @Test
   void whenCheckingBoolean_thenFalse() {
      boolean aBoolean = false;

      assertThat(aBoolean).isFalse();
   }

   @Test
   void whenComparingArrays_thenEqual() {
      String[] firstArrayOfStrings = {"one", "two", "three"};
      String[] secondArrayOfStrings = {"one", "two", "three"};

      assertThat(firstArrayOfStrings).isEqualTo(secondArrayOfStrings);
   }

   @Test
   void whenComparingArrays_thenNotEqual() {
      String[] firstArrayOfStrings = {"one", "two", "three"};
      String[] secondArrayOfStrings = {"three", "two", "one"};

      assertThat(firstArrayOfStrings).isNotEqualTo(secondArrayOfStrings);
   }

   @Test
   void whenCheckingArray_thenEmpty() {
      Object[] anArray = {};

      assertThat(anArray).isEmpty();
   }

   @Test
   void whenCheckingArray_thenNotEmpty() {
      String[] arrayOfStrings = {"One String "};

      assertThat(arrayOfStrings).isNotEmpty();
   }

   @Test
   void whenCheckingArrayOfDoubles_thenWithinPrecision() {
      double[] arrayOfDoubles = {1, 2, 3, 4, 5};

      assertThat(arrayOfDoubles).hasValuesWithin(5)
            .of(6, 7, 8, 9, 10);
   }

   @Test
   void whenComparingUsers_thenEqual() {
      User aUser = new User("John Doe");
      User anotherUser = new User("John Doe");

      assertThat(aUser).isEqualTo(anotherUser);
   }

   @Test
   void whenComparingUser_thenIsNull() {
      User aUser = null;

      assertThat(aUser).isNull();
   }

   @Test
   void whenComparingUser_thenNotNull() {
      User aUser = new User();

      assertThat(aUser).isNotNull();
   }

   @Test
   void whenComparingUser_thenInstanceOf() {
      User aUser = new User();

      assertThat(aUser).isInstanceOf(User.class);
   }

   @Test
   void whenComparingUser_thenInList() {
      User aUser = new User();

      assertThat(aUser).isIn(Arrays.asList(1, 3, aUser, null));
   }

   @Test
   void whenComparingUser_thenNotInList() {
      User aUser = new User();

      assertThat(aUser).isNotIn(Arrays.asList(1, 3, "Three"));
   }

   @Test
   void whenComparingNullUser_thenInList() {
      User aUser = null;
      User anotherUser = new User();

      assertThat(aUser).isIn(Arrays.asList(1, 3, anotherUser, null));
   }

   @Test
   void whenCheckingString_thenStartsWithString() {
      String aString = "This is a string";

      assertThat(aString).startsWith("This");
   }

   @Test
   void whenCheckingString_thenContainsString() {
      String aString = "This is a string";

      assertThat(aString).contains("is a");
   }

   @Test
   void whenCheckingString_thenEndsWithString() {
      String aString = "This is a string";

      assertThat(aString).endsWith("string");
   }

   @Test
   void whenCheckingString_thenExpectedLength() {
      String aString = "This is a string";

      assertThat(aString).hasLength(16);
   }

   @Test
   void whenCheckingString_thenEmpty() {
      String aString = "";

      assertThat(aString).isEmpty();
   }

   @Test
   void whenCheckingString_thenMatches() {
      String aString = "The string to match";

      assertThat(aString).matches(Pattern.compile("[a-zA-Z\\s]+"));
   }

   @Test
   void whenCheckingComparable_thenAtLeast() {
      Comparable<Integer> aComparable = 5;

      assertThat(aComparable).isAtLeast(1);
   }

   @Test
   void whenCheckingComparable_thenAtMost() {
      Comparable<Integer> aComparable = 5;

      assertThat(aComparable).isAtMost(10);
   }

   @Test
   void whenCheckingComparable_thenInList() {
      Comparable<Integer> aComparable = 5;

      assertThat(aComparable).isIn(Arrays.asList(4, 5, 6));
   }

   @Test
   void whenCheckingComparable_thenInRange() {
      Comparable<Integer> aComparable = 5;

      assertThat(aComparable).isIn(Range.closed(1, 10));
   }

   @Test
   void whenCheckingComparable_thenNotInRange() {
      Comparable<Integer> aComparable = 5;

      assertThat(aComparable).isNotIn(Range.closed(10, 15));
   }

   @Test
   void whenComparingUsers_thenEquivalent() {
      User aUser = new User();
      aUser.setName("John Doe");

      User anotherUser = new User();
      anotherUser.setName("john doe");

      assertThat(aUser).isEquivalentAccordingToCompareTo(anotherUser);
   }

   @Test
   void whenCheckingIterable_thenContains() {
      List<Integer> aList = Arrays.asList(4, 5, 6);

      assertThat(aList).contains(5);
   }

   @Test
   void whenCheckingIterable_thenDoesNotContains() {
      List<Integer> aList = Arrays.asList(4, 5, 6);

      assertThat(aList).doesNotContain(9);
   }

   @Test
   void whenCheckingIterable_thenContainsAny() {
      List<Integer> aList = Arrays.asList(4, 5, 6);

      assertThat(aList).containsAnyOf(0, 5, 10);
   }

   @Test
   void whenCheckingIterable_thenContainsAnyInList() {
      List<Integer> aList = Arrays.asList(1, 2, 3);

      assertThat(aList).containsAnyIn(Arrays.asList(1, 5, 10));
   }

   @Test
   void whenCheckingIterable_thenNoDuplicates() {
      List<Integer> aList = Arrays.asList(-2, -1, 0, 1, 2);

      assertThat(aList).containsNoDuplicates();
   }

   @Test
   void whenCheckingIterable_thenContainsNoneOf() {
      List<Integer> aList = Arrays.asList(4, 5, 6);

      assertThat(aList).containsNoneOf(9, 8, 7);
   }

   @Test
   void whenCheckingIterable_thenContainsNoneIn() {
      List<Integer> aList = Arrays.asList(4, 5, 6);

      assertThat(aList).containsNoneIn(Arrays.asList(9, 10, 11));
   }

   @Test
   void whenCheckingIterable_thenContainsExactElements() {
      List<String> aList = Arrays.asList("10", "20", "30");
      List<String> anotherList = Arrays.asList("10", "20", "30");

      assertThat(aList).containsExactlyElementsIn(anotherList)
            .inOrder();
   }

   @Test
   void whenCheckingIterable_thenOrdered() {
      Set<String> aSet = new LinkedHashSet<>(Arrays.asList("one", "three", "two"));

      assertThat(aSet).isOrdered();
   }

   @Test
   void givenComparator_whenCheckingIterable_thenOrdered() {
      Comparator<String> aComparator = (a, b) -> new Float(a).compareTo(new Float(b));

      List<String> aList = Arrays.asList("1", "012", "0020", "100");

      assertThat(aList).isOrdered(aComparator);
   }

   @Test
   void whenCheckingMap_thenContainsEntry() {
      Map<String, Object> aMap = new HashMap<>();
      aMap.put("one", 1L);

      assertThat(aMap).containsEntry("one", 1L);
   }

   @Test
   void whenCheckingMap_thenContainsKey() {
      Map<String, Object> map = new HashMap<>();
      map.put("one", 1L);

      assertThat(map).containsKey("one");
   }

   @Test
   void whenCheckingMap_thenContainsEntries() {
      Map<String, Object> aMap = new HashMap<>();
      aMap.put("first", 1L);
      aMap.put("second", 2.0);
      aMap.put("third", 3f);

      Map<String, Object> anotherMap = new HashMap<>(aMap);

      assertThat(aMap).containsExactlyEntriesIn(anotherMap);
   }

   @Test
   void whenCheckingException_thenInstanceOf() {
      Exception anException = new IllegalArgumentException(new NumberFormatException());

      assertThat(anException).hasCauseThat()
            .isInstanceOf(NumberFormatException.class);
   }

   @Test
   void whenCheckingException_thenCauseMessageIsKnown() {
      Exception anException = new IllegalArgumentException("Bad value");

      assertThat(anException).hasMessageThat()
            .startsWith("Bad");
   }

   @Test
   void whenCheckingClass_thenIsAssignable() {
      Class<Double> aClass = Double.class;

      assertThat(aClass).isAssignableTo(Number.class);
   }

   // Java 8 Tests
   @Test
   void whenCheckingJavaOptional_thenHasValue() {
      Optional<Integer> anOptional = Optional.of(1);

      assertThat(anOptional).hasValue(1);
   }

   @Test
   void whenCheckingJavaOptional_thenPresent() {
      Optional<String> anOptional = Optional.of("Tuyucheng");

      assertThat(anOptional).isPresent();
   }

   @Test
   void whenCheckingJavaOptional_thenEmpty() {
      Optional anOptional = Optional.empty();

      assertThat(anOptional).isEmpty();
   }

   @Test
   void whenCheckingStream_thenContainsInOrder() {
      Stream<Integer> anStream = Stream.of(1, 2, 3);

      assertThat(anStream).containsAllOf(1, 2, 3)
            .inOrder();
   }

   @Test
   void whenCheckingStream_thenDoesNotContain() {
      Stream<Integer> anStream = IntStream.range(1, 100)
            .boxed();

      assertThat(anStream).doesNotContain(0);
   }

   // Guava Tests
   @Test
   void whenCheckingGuavaOptional_thenIsAbsent() {
      com.google.common.base.Optional anOptional = com.google.common.base.Optional.absent();

      assertThat(anOptional).isAbsent();
   }

   @Test
   void whenCheckingGuavaMultimap_thenExpectedSize() {
      Multimap<String, Object> aMultimap = ArrayListMultimap.create();
      aMultimap.put("one", 1L);
      aMultimap.put("one", 2.0);

      assertThat(aMultimap).valuesForKey("one")
            .hasSize(2);
   }

   @Test
   void whenCheckingGuavaMultiset_thenExpectedCount() {
      TreeMultiset<String> aMultiset = TreeMultiset.create();
      aMultiset.add("Tuyucheng", 10);

      assertThat(aMultiset).hasCount("Tuyucheng", 10);
   }

   @Test
   void whenCheckingGuavaTable_thenContains() {
      Table<String, String, String> aTable = getDummyGuavaTable();

      assertThat(aTable).contains("firstRow", "firstColumn");
   }

   @Test
   void whenCheckingGuavaTable_thenContainsCell() {
      Table<String, String, String> aTable = getDummyGuavaTable();

      assertThat(aTable).containsCell("firstRow", "firstColumn", "Tuyucheng");
   }

   @Test
   void whenCheckingGuavaTable_thenContainsRow() {
      Table<String, String, String> aTable = getDummyGuavaTable();

      assertThat(aTable).containsRow("firstRow");
   }

   @Test
   void whenCheckingGuavaTable_thenContainsColumn() {
      Table<String, String, String> aTable = getDummyGuavaTable();

      assertThat(aTable).containsColumn("firstColumn");
   }

   @Test
   void whenCheckingGuavaTable_thenContainsValue() {
      Table<String, String, String> aTable = getDummyGuavaTable();

      assertThat(aTable).containsValue("Tuyucheng");
   }

   @Disabled
   @Test
   void whenFailingAssertion_thenMessagePrefix() {
      User aUser = new User();

      assertThat(aUser).named("User [%s]", aUser.getName())
            .isNull();
   }

   @Disabled
   @Test
   void whenFailingAssertion_thenCustomMessage() {
      User aUser = new User();

      assertWithMessage("TEST-985: Secret user subject was NOT null!").that(aUser)
            .isNull();
   }

   @Disabled
   @Test
   void whenFailingAssertion_thenCustomMessageAndPrefix() {
      User aUser = new User();

      assertWithMessage("TEST-985: Secret user subject was NOT null!").that(aUser)
            .named("User [%s]", aUser.getName())
            .isNull();
   }

   private Table<String, String, String> getDummyGuavaTable() {
      Table<String, String, String> aTable = TreeBasedTable.create();
      aTable.put("firstRow", "firstColumn", "Tuyucheng");
      return aTable;
   }

   // Custom User type
   @Test
   void whenCheckingUser_thenHasName() {
      User aUser = new User();

      assertThat(aUser).hasName("John Doe");
   }

   @Test
   void whenCheckingUser_thenHasNameIgnoringCase() {
      User aUser = new User();

      assertThat(aUser).hasNameIgnoringCase("john doe");
   }

   @Test
   void givenUser_whenCheckingEmails_thenExpectedSize() {
      User aUser = new User();

      assertThat(aUser).emails()
            .hasSize(2);
   }
}