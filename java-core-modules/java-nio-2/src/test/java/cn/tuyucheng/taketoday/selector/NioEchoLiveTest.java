package cn.tuyucheng.taketoday.selector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NioEchoLiveTest {

   private Process server;
   private EchoClient client;

   @BeforeEach
   public void setup() throws IOException, InterruptedException {
      server = EchoServer.start();
      client = EchoClient.start();
   }

   @Test
   public void givenServerClient_whenServerEchosMessage_thenCorrect() {
      String resp1 = client.sendMessage("hello");
      String resp2 = client.sendMessage("world");
      assertEquals("hello", resp1);
      assertEquals("world", resp2);
   }

   @AfterEach
   public void teardown() throws IOException {
      server.destroy();
      EchoClient.stop();
   }
}
