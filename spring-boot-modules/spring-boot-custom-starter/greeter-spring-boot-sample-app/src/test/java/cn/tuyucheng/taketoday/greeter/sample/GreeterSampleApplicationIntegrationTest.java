package cn.tuyucheng.taketoday.greeter.sample;

import cn.tuyucheng.taketoday.greeter.library.Greeter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GreeterSampleApplication.class)
class GreeterSampleApplicationIntegrationTest {

    @Autowired
    private Greeter greeter;

    @Test
    void givenMorningTime_ifMorningMessage_thenSuccess() {
        String expected = "Hello Tuyucheng, Good Morning";
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 6, 0));
        assertEquals(expected, actual);
    }

    @Test
    void givenAfternoonTime_ifAfternoonMessage_thenSuccess() {
        String expected = "Hello Tuyucheng, Woha Afternoon";
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 13, 0));
        assertEquals(expected, actual);
    }

    @Test
    void givenEveningTime_ifEveningMessage_thenSuccess() {
        String expected = "Hello Tuyucheng, Good Evening";
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 19, 0));
        assertEquals(expected, actual);
    }

    @Test
    void givenNightTime_ifNightMessage_thenSuccess() {
        String expected = "Hello Tuyucheng, Good Night";
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 21, 0));
        assertEquals(expected, actual);
    }
}