package cn.tuyucheng.taketoday.macaddress;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MacAddressUnitTest {

   @Test
   public void givenNetworkInterface_whenUsingLocalHost_thenGetMacAddress() throws UnknownHostException, SocketException {
      InetAddress localHost = InetAddress.getLocalHost();
      NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
      byte[] macAddress = ni.getHardwareAddress();
      assertEquals(6, macAddress.length);
   }
}
