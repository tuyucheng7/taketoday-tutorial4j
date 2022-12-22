package cn.tuyucheng.taketoday.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationPropertyImportExternalFileIntegrationTest {

    @Value("${tuyucheng.property1}")
    String tuyuchengProperty;

    @Test
    void whenExternalisedPropertiesLoadedUsingApplicationProperties_thenReadValues() throws IOException {
        assertEquals("value1", tuyuchengProperty);
    }
}