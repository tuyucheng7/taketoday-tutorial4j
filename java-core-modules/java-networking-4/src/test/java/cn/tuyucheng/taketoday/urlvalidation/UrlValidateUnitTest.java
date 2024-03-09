package cn.tuyucheng.taketoday.urlvalidation;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlValidateUnitTest {

   @Test
   public void givenValidStringAsURL_whenUsingJDK_shouldReturnTrue() throws MalformedURLException, URISyntaxException {
      UrlValidation urlValidator = new UrlValidation();
      assertTrue(urlValidator.isValidURLJavaNet("http://tuyucheng.com/"));
   }

   @Test
   public void givenInvalidStringAsURL_whenUsingJDK_shouldReturnFalse() throws MalformedURLException, URISyntaxException {
      UrlValidation urlValidator = new UrlValidation();
      assertFalse(urlValidator.isValidURLJavaNet("https://www.tuyucheng.com/ java-%%$^&& iuyi"));
   }

   @Test
   public void givenInvalidStringAsURL_whenUsingJDK_shouldReturnTrue() throws MalformedURLException {
      UrlValidation urlValidator = new UrlValidation();
      assertTrue(urlValidator.invalidUrlAsValidJavaNet("https://www.tuyucheng.com/ java-%%$^&& iuyi"));
   }

   @Test
   public void givenValidStringAsURL_whenUsingApache_shouldReturnTrue() throws MalformedURLException {
      UrlValidation urlValidator = new UrlValidation();
      assertTrue(urlValidator.isValidURLApache("http://tuyucheng.com/"));
   }

   @Test
   public void givenInvalidStringAsURL_whenUsingApache_shouldReturnFalse() throws MalformedURLException {
      UrlValidation urlValidator = new UrlValidation();
      assertFalse(urlValidator.isValidURLApache("https://www.tuyucheng.com/ java-%%$^&& iuyi"));
   }
}