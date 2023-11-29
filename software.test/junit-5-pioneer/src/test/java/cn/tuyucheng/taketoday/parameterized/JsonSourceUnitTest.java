package cn.tuyucheng.taketoday.parameterized;

import cn.tuyucheng.taketoday.parameterized.pojo.Jedi;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.JsonSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class JsonSourceUnitTest {

   @ParameterizedTest
   @JsonSource("""
         [
         	{ "name": "Luke", "height": 172  },
         	{ "name": "Yoda", "height": 66 }
         ]
         """)
   void givenTestBlockSource_whenInjectToArgs_thenCorrect(@Property("name") String name,
                                                          @Property("height") int height) {
      if (name.equals("Luke"))
         assertEquals(172, height);
      else if (name.equals("Yoda"))
         assertEquals(66, height);
      else
         fail();
   }

   @ParameterizedTest
   @JsonClasspathSource("jedis.json")
   void givenJsonFileSource_whenInjectObj_thenFieldShouldNotNull(Jedi jedi) {
      assertThat(jedi.name).isNotNull();
      assertThat(jedi.height).isNotNull();
   }

   @ParameterizedTest
   @JsonSource("[ { name: 'Luke', height: 172  }, { name: 'Yoda', height: 66 } ]")
   void givenStringSource_whenInjectObj_thenFieldShouldNotNull(Jedi jedi) {
      assertThat(jedi.name).isNotNull();
      assertThat(jedi.height).isNotNull();
   }

   @ParameterizedTest
   @JsonClasspathSource("jedis.json")
   void givenJsonFileSource_whenInjectOnlyName_thenShouldNotNull(@Property("name") String jediName) {
      assertThat(jediName).isNotNull();
   }

   @ParameterizedTest
   @JsonSource({
         "{ name: 'Yoda', padawans: ['Dooku', 'Luke']  }",
         "{ name: 'Obi-Wan', padawans: ['Anakin', 'Luke'] }"
   })
   void givenStringSource_whenInjectToList_thenListItemsShouldNotNull(@Property("padawans") List<String> padawanNames) {
      assertThat(padawanNames).hasSize(2)
            .allMatch(Objects::nonNull);
   }

   @ParameterizedTest
   @JsonClasspathSource("jedis.json")
   void givenJsonFileSource_whenDeconstructFromArray_thenCorrect(@Property("name") String name,
                                                                 @Property("height") int height) {
      assertNotNull(name);
      assertTrue(height > 0);
   }

   @ParameterizedTest
   @JsonClasspathSource(value = "luke.json", data = "vehicles")
   void givenJsonFileSource_whenExtractArray_thenCorrect(@Property("name") String name,
                                                         @Property("length") double length) {
      assertNotNull(name);
      assertTrue(length > 0);
   }
}