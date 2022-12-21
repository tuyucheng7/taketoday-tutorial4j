package cn.tuyucheng.taketoday.greeter;

import cn.tuyucheng.taketoday.greeter.library.Greeter;
import cn.tuyucheng.taketoday.greeter.library.GreetingConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static cn.tuyucheng.taketoday.greeter.library.GreeterConfigParams.AFTERNOON_MESSAGE;
import static cn.tuyucheng.taketoday.greeter.library.GreeterConfigParams.EVENING_MESSAGE;
import static cn.tuyucheng.taketoday.greeter.library.GreeterConfigParams.MORNING_MESSAGE;
import static cn.tuyucheng.taketoday.greeter.library.GreeterConfigParams.NIGHT_MESSAGE;
import static cn.tuyucheng.taketoday.greeter.library.GreeterConfigParams.USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterIntegrationTest {

    private static GreetingConfig greetingConfig;

    @BeforeAll
    static void initalizeGreetingConfig() {
        greetingConfig = new GreetingConfig();
        greetingConfig.put(USER_NAME, "World");
        greetingConfig.put(MORNING_MESSAGE, "Good Morning");
        greetingConfig.put(AFTERNOON_MESSAGE, "Good Afternoon");
        greetingConfig.put(EVENING_MESSAGE, "Good Evening");
        greetingConfig.put(NIGHT_MESSAGE, "Good Night");
    }

    @Test
    void givenMorningTime_ifMorningMessage_thenSuccess() {
        String expected = "Hello World, Good Morning";
        Greeter greeter = new Greeter(greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 6, 0));
        assertEquals(expected, actual);
    }

    @Test
    void givenAfternoonTime_ifAfternoonMessage_thenSuccess() {
        String expected = "Hello World, Good Afternoon";
        Greeter greeter = new Greeter(greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 13, 0));

        assertEquals(expected, actual);
    }

    @Test
    void givenEveningTime_ifEveningMessage_thenSuccess() {
        String expected = "Hello World, Good Evening";
        Greeter greeter = new Greeter(greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 19, 0));

        assertEquals(expected, actual);
    }

    @Test
    void givenNightTime_ifNightMessage_thenSuccess() {
        String expected = "Hello World, Good Night";
        Greeter greeter = new Greeter(greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 21, 0));

        assertEquals(expected, actual);
    }
}