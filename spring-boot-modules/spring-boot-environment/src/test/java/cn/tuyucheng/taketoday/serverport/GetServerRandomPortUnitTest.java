package cn.tuyucheng.taketoday.serverport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GetServerPortApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("randomport")
class GetServerRandomPortUnitTest {

    @Value("${server.port}")
    private int randomServerPort;

    @Autowired
    private ServerPortService serverPortService;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Test
    void given0AsServerPort_whenReadServerPort_thenGet0() {
        assertEquals(0, randomServerPort, "Reading random port by @Value(\"${server.port}\") will get 0.");
    }

    @Test
    void given0AsServerPort_whenReadServerProps_thenGet0() {
        int port = serverProperties.getPort();
        assertEquals(0, port, "Reading random port by serverProperties will get 0.");
    }

    @Test
    void given0AsServerPort_whenReadWebAppCtxt_thenGetThePort() {
        int port = webServerAppCtxt.getWebServer().getPort();
        assertTrue(port > 1023, "The random port should be greater than 1023");
    }

    @Test
    void given0AsServerPort_whenReadFromListener_thenGetThePort() {
        int port = serverPortService.getPort();
        assertTrue(port > 1023, "The random port should be greater than 1023");
    }
}