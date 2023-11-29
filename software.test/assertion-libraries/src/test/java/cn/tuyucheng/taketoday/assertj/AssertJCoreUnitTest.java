package cn.tuyucheng.taketoday.assertj;

import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class AssertJCoreUnitTest {

   @Test
   void whenComparingReferences_thenNotEqual() throws Exception {
      Dog fido = new Dog("Fido", 5.15f);
      Dog fidoClone = new Dog("Fido", 5.15f);

      assertThat(fido).isNotEqualTo(fidoClone);
   }

   @Test
   void whenComparingFields_thenEqual() throws Exception {
      Dog fido = new Dog("Fido", 5.15f);
      Dog fidosClone = new Dog("Fido", 5.15f);

      assertThat(fido).isEqualToComparingFieldByFieldRecursively(fidosClone);
   }

   @Test
   void whenCheckingForElement_thenContains() throws Exception {
      List<String> list = Arrays.asList("1", "2", "3");

      assertThat(list).contains("1");
   }

   @Test
   void whenCheckingForElement_thenMultipleAssertions() throws Exception {
      List<String> list = Arrays.asList("1", "2", "3");

      assertThat(list).isNotEmpty();
      assertThat(list).startsWith("1");
      assertThat(list).doesNotContainNull();

      assertThat(list).isNotEmpty().contains("1").startsWith("1").doesNotContainNull().containsSequence("2", "3");
   }

   @Test
   void whenCheckingRunnable_thenIsInterface() throws Exception {
      assertThat(Runnable.class).isInterface();
   }

   @Test
   void whenCheckingCharacter_thenIsUnicode() throws Exception {
      char someCharacter = 'c';

      assertThat(someCharacter).isNotEqualTo('a').inUnicode().isGreaterThanOrEqualTo('b').isLowerCase();
   }

   @Test
   void whenAssigningNSEExToException_thenIsAssignable() throws Exception {
      assertThat(Exception.class).isAssignableFrom(NoSuchElementException.class);
   }

   @Test
   void whenComparingWithOffset_thenEquals() throws Exception {
      assertThat(5.1).isEqualTo(5, withPrecision(1d));
   }

   @Test
   void whenCheckingString_then() throws Exception {
      assertThat("".isEmpty()).isTrue();
   }

   @Test
   void whenCheckingFile_then() throws Exception {
      final File someFile = File.createTempFile("aaa", "bbb");
      someFile.deleteOnExit();

      assertThat(someFile).exists().isFile().canRead().canWrite();
   }

   @Test
   void whenCheckingIS_then() throws Exception {
      InputStream given = new ByteArrayInputStream("foo".getBytes());
      InputStream expected = new ByteArrayInputStream("foo".getBytes());

      assertThat(given).hasSameContentAs(expected);
   }

   @Test
   void whenGivenMap_then() throws Exception {
      Map<Integer, String> map = Maps.newHashMap(2, "a");

      assertThat(map).isNotEmpty().containsKey(2).doesNotContainKeys(10).contains(entry(2, "a"));
   }

   @Test
   void whenGivenException_then() throws Exception {
      Exception ex = new Exception("abc");

      assertThat(ex).hasNoCause().hasMessageEndingWith("c");
   }

   @Disabled // IN ORDER TO TEST, REMOVE THIS LINE
   @Test
   void whenRunningAssertion_thenDescribed() throws Exception {
      Person person = new Person("Alex", 34);

      assertThat(person.getAge()).as("%s's age should be equal to 100").isEqualTo(100);
   }
}