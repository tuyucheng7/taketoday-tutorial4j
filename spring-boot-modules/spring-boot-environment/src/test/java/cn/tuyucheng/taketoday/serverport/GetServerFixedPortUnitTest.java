package cn.tuyucheng.taketoday.serverport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GetServerPortApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("fixedport")
class GetServerFixedPortUnitTest {
    private final static int EXPECTED_PORT = 7777;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private ServerProperties serverProperties;

    @Test
    void givenFixedPortAsServerPort_whenReadServerPort_thenGetThePort() {
        assertEquals(EXPECTED_PORT, serverPort, "Reading fixed port by @Value(\"${server.port}\") will get the port.");
    }

    @Test
    void givenFixedPortAsServerPort_whenReadServerProps_thenGetThePort() {
        int port = serverProperties.getPort();
        assertEquals(EXPECTED_PORT, port, "Reading fixed port from serverProperties will get the port.");
    }
}