package cn.tuyucheng.taketoday.enablessldebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class SSLDebugLogger {
   private static final Logger logger = LoggerFactory.getLogger(SSLDebugLogger.class);

   public static void enableSSLDebugUsingSystemProperties() {
      System.setProperty("javax.net.debug", "ssl");
   }

   public static void makeHttpsRequest() throws Exception {
      String url = "https://github.com/eugenp/tutorials";
      URL httpsUrl = new URL(url);
      HttpsURLConnection connection = (HttpsURLConnection) httpsUrl.openConnection();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
         String line;
         logger.debug("Response from " + url + ":");
         while ((line = reader.readLine()) != null) {
            logger.debug(line);
         }
      }
   }
}