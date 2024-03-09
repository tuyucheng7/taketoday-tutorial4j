package cn.tuyucheng.taketoday.spring.data.couchbase2b.service;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Person;
import cn.tuyucheng.taketoday.spring.data.couchbase2b.MultiBucketCouchbaseConfig;
import cn.tuyucheng.taketoday.spring.data.couchbase2b.MultiBucketLiveTest;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static cn.tuyucheng.taketoday.spring.data.couchbase2b.MultiBucketCouchbaseConfig.DEFAULT_BUCKET_PASSWORD;
import static cn.tuyucheng.taketoday.spring.data.couchbase2b.MultiBucketCouchbaseConfig.DEFAULT_BUCKET_USERNAME;
import static cn.tuyucheng.taketoday.spring.data.couchbase2b.MultiBucketCouchbaseConfig.NODE_LIST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonServiceImplLiveTest extends MultiBucketLiveTest {

   static final String typeField = "_class";
   static final String john = "John";
   static final String smith = "Smith";
   static final String johnSmithId = "person:" + john + ":" + smith;
   static final Person johnSmith = new Person(johnSmithId, john, smith);
   static final JsonObject jsonJohnSmith = JsonObject.create().put(typeField, Person.class.getName()).put("firstName", john).put("lastName", smith).put("created", DateTime.now().getMillis());

   static final String foo = "Foo";
   static final String bar = "Bar";
   static final String foobarId = "person:" + foo + ":" + bar;
   static final Person foobar = new Person(foobarId, foo, bar);
   static final JsonObject jsonFooBar = JsonObject.create().put(typeField, Person.class.getName()).put("firstName", foo).put("lastName", bar).put("created", DateTime.now().getMillis());

   @Autowired
   private PersonServiceImpl personService;

   @BeforeAll
   static void setupBeforeClass() {
      final Cluster cluster = Cluster.connect(NODE_LIST, DEFAULT_BUCKET_USERNAME, DEFAULT_BUCKET_PASSWORD);
      final Bucket bucket = cluster.bucket(MultiBucketCouchbaseConfig.DEFAULT_BUCKET_NAME);
      final Collection collection = bucket.defaultCollection();
      collection.upsert(johnSmithId, JsonObject.create().put(johnSmithId, jsonJohnSmith));
      collection.upsert(foobarId, JsonObject.create().put(foobarId, jsonFooBar));
      cluster.disconnect();
   }

   @Test
   void whenFindingPersonByJohnSmithId_thenReturnsJohnSmith() {
      final Optional<Person> actualPerson = personService.findOne(johnSmithId);
      assertTrue(actualPerson.isPresent());
      assertNotNull(actualPerson.get().getCreated());
      assertEquals(johnSmith, actualPerson.get());
   }

   @Test
   void whenFindingAllPersons_thenReturnsTwoOrMorePersonsIncludingJohnSmithAndFooBar() {
      final List<Person> resultList = personService.findAll();
      assertNotNull(resultList);
      assertFalse(resultList.isEmpty());
      assertTrue(resultContains(resultList, johnSmith));
      assertTrue(resultContains(resultList, foobar));
      assertTrue(resultList.size() >= 2);
   }

   @Test
   void whenFindingByFirstNameJohn_thenReturnsOnlyPersonsNamedJohn() {
      final String expectedFirstName = john;
      final List<Person> resultList = personService.findByFirstName(expectedFirstName);
      assertNotNull(resultList);
      assertFalse(resultList.isEmpty());
      assertTrue(allResultsContainExpectedFirstName(resultList, expectedFirstName));
   }

   @Test
   void whenFindingByLastNameSmith_thenReturnsOnlyPersonsNamedSmith() {
      final String expectedLastName = smith;
      final List<Person> resultList = personService.findByLastName(expectedLastName);
      assertNotNull(resultList);
      assertFalse(resultList.isEmpty());
      assertTrue(allResultsContainExpectedLastName(resultList, expectedLastName));
   }

   private boolean resultContains(List<Person> resultList, Person person) {
      boolean found = false;
      for (final Person p : resultList) {
         if (p.equals(person)) {
            found = true;
            break;
         }
      }
      return found;
   }

   private boolean allResultsContainExpectedFirstName(List<Person> resultList, String firstName) {
      boolean found = false;
      for (final Person p : resultList) {
         if (p.getFirstName().equals(firstName)) {
            found = true;
            break;
         }
      }
      return found;
   }

   private boolean allResultsContainExpectedLastName(List<Person> resultList, String lastName) {
      boolean found = false;
      for (final Person p : resultList) {
         if (p.getLastName().equals(lastName)) {
            found = true;
            break;
         }
      }
      return found;
   }
}