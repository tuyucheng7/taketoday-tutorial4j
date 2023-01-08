package cn.tuyucheng.taketoday.openliberty;

import cn.tuyucheng.taketoday.openliberty.person.model.Person;
import cn.tuyucheng.taketoday.openliberty.rest.consumes.RestConsumer;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

public class RestClientLiveTest {

    private static String BASE_URL;

    private final String API_PERSON = "api/persons";

    @BeforeClass
    public static void oneTimeSetup() {
        BASE_URL = "http://localhost:9080/";
    }

    @Test
    public void testSuite() {
        // run the test only when liberty server is started
        // this.whenConsumeWithJsonb_thenGetPerson();
    }

    public void whenConsumeWithJsonb_thenGetPerson() {
        String url = BASE_URL + API_PERSON + "/1";
        String result = RestConsumer.consumeWithJsonb(url);

        Person person = JsonbBuilder.create().fromJson(result, Person.class);
        assertEquals(1, person.getId());
        assertEquals("normanlewis", person.getUsername());
        assertEquals("normanlewis@email.com", person.getEmail());
    }
}