package cn.tuyucheng.taketoday.networking.udp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UDPLiveTest {
   private EchoClient client;

   @BeforeEach
   public void setup() throws IOException {
      new EchoServer().start();
      client = new EchoClient();
   }

   @Test
   public void whenCanSendAndReceivePacket_thenCorrect1() {
      String echo = client.sendEcho("hello server");
      assertEquals("hello server", echo);
      echo = client.sendEcho("server is working");
      assertFalse(echo.equals("hello server"));
   }

   @AfterEach
   public void tearDown() {
      stopEchoServer();
      client.close();
   }

   private void stopEchoServer() {
      client.sendEcho("end");
   }
}
