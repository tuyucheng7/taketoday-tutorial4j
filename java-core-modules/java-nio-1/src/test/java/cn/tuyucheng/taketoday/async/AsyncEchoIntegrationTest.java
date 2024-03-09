package cn.tuyucheng.taketoday.async;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsyncEchoIntegrationTest {

   Process server;
   AsyncEchoClient client;

   @BeforeEach
   public void setup() throws IOException, InterruptedException {
      server = AsyncEchoServer2.start();
      client = AsyncEchoClient.getInstance();
   }

   @Test
   public void givenServerClient_whenServerEchosMessage_thenCorrect() throws Exception {
      String resp1 = client.sendMessage("hello");
      String resp2 = client.sendMessage("world");
      assertEquals("hello", resp1);
      assertEquals("world", resp2);
   }

   @AfterEach
   public void teardown() throws IOException {
      server.destroy();
      client.stop();
   }

}