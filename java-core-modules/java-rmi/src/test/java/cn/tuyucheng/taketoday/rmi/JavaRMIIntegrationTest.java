package cn.tuyucheng.taketoday.rmi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JavaRMIIntegrationTest {

   private MessengerServiceImpl messengerService;

   @BeforeEach
   public void init() {
      try {
         messengerService = new MessengerServiceImpl();
         messengerService.createStubAndBind();
      } catch (RemoteException e) {
         fail("Exception Occurred: " + e);
      }
   }

   @Test
   public void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
      try {
         Registry registry = LocateRegistry.getRegistry();
         MessengerService server = (MessengerService) registry.lookup("MessengerService");
         String responseMessage = server.sendMessage("Client Message");

         String expectedMessage = "Server Message";
         assertEquals(responseMessage, expectedMessage);
      } catch (RemoteException | NotBoundException e) {
         fail("Exception Occurred: " + e);
      }
   }

}