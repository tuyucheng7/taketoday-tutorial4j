package cn.tuyucheng.taketoday.downloadwebpage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DownloadWebpageUnitTest {

   @Test
   public void givenURLConnection_whenRetrieveWebpage_thenWebpageIsNotNullAndContainsHtmlTag() throws IOException {
      URL url = new URL("https://example.com");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
         StringBuilder responseBuilder = new StringBuilder();
         String line;
         while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
         }

         assertNotNull(responseBuilder);
         assertTrue(responseBuilder.toString()
               .contains("<html>"));
      }

   }

   @Test
   public void givenJsoup_whenRetrievingWebpage_thenWebpageDocumentIsNotNullAndContainsHtmlTag() throws IOException {

      Document document = Jsoup.connect("https://example.com")
            .get();
      String webpage = document.html();

      assertNotNull(webpage);
      assertTrue(webpage.contains("<html>"));

   }

}
