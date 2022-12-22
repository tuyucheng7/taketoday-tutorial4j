package cn.tuyucheng.taketoday.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ExternalPropertyFileLoader.class)
class ExternalPropertyFileLoaderIntegrationTest {

    @Autowired
    ConfProperties props;

    @Test
    void whenExternalisedPropertiesLoaded_thenReadValues() throws IOException {
        assertEquals("jdbc:postgresql://localhost:5432/", props.getUrl());
        assertEquals("admin", props.getUsername());
        assertEquals("root", props.getPassword());
    }
}