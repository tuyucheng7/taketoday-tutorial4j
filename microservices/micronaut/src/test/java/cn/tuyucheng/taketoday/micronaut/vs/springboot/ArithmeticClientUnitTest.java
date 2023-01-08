package cn.tuyucheng.taketoday.micronaut.vs.springboot;

import cn.tuyucheng.taketoday.micronaut.vs.springboot.client.ArithmeticClientImpl;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

public class ArithmeticClientUnitTest {
    private EmbeddedServer server;
    private ArithmeticClientImpl client;

    @Before
    public void setup() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext()
              .getBean(ArithmeticClientImpl.class);
    }

    @After
    public void cleanup() {
        server.stop();
    }

    @Test
    public void givenTwoNumbers_whenAdd_thenCorrectAnswerReturned() {
        String expected = Float.valueOf(10 + 20).toString();
        assertEquals(expected, client.sum(10, 20));
    }

    @Test
    public void givenTwoNumbers_whenSubtract_thenCorrectAnswerReturned() {
        String expected = Float.valueOf(20 - 10).toString();
        assertEquals(expected, client.subtract(20, 10));
    }

    @Test
    public void givenTwoNumbers_whenMultiply_thenCorrectAnswerReturned() {
        String expected = Float.valueOf(10 * 20).toString();
        assertEquals(expected, client.multiply(10, 20));
    }

    @Test
    public void givenTwoNumbers_whenDivide_thenCorrectAnswerReturned() {
        String expected = Float.valueOf(30 / 10).toString();
        assertEquals(expected, client.divide(30, 10));
    }

    @Test
    public void whenMemory_thenCorrectAnswerReturned() {
        String expected = "Initial:";
        assertThat(client.memory(), containsString(expected));
    }
}