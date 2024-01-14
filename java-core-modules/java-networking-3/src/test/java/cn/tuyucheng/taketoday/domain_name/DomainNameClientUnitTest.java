package cn.tuyucheng.taketoday.domain_name;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainNameClientUnitTest {

   DomainNameClient domainNameClient = new DomainNameClient();

   @Test
   void givenUrl_whenGetHost_thenReturnSubdomainAndDomainName() {
      Assertions.assertAll(() -> {
         assertEquals("www.tuyucheng.com", domainNameClient.getHost("https://www.tuyucheng.com/domain"));
         assertEquals("www.google.co.uk", domainNameClient.getHost("https://www.google.co.uk/domain"));
         assertEquals("jira.tuyucheng.com", domainNameClient.getHost("https://jira.tuyucheng.com/secure"));
      });
   }

   @Test
   void givenUrl_whenGetTopPrivateDomain_thenReturnDomainName() {
      assertEquals("tuyucheng.com", domainNameClient.getTopPrivateDomain("www.tuyucheng.com"));
   }

   @Test
   void givenUrlWithPublicSuffix_whenGetTopPrivateDomain_thenReturnDomainName() {
      assertEquals("google.co.uk", domainNameClient.getTopPrivateDomain("www.google.co.uk"));
      assertEquals("tuyucheng.blogspot.com", domainNameClient.getTopPrivateDomain("www.tuyucheng.blogspot.com"));
   }

   @Test
   void givenUrlWithPublicSuffix_whenGetName_thenReturnSecondLevelDomain() {
      assertEquals("tuyucheng", domainNameClient.getName("jira.tuyucheng.com"));
   }

   @Test
   void givenUrlWithPublicSuffix_whenGetName_thenReturnThirdLevelDomain() {
      assertEquals("tuyucheng", domainNameClient.getName("www.tuyucheng.co.uk"));
      assertEquals("google", domainNameClient.getName("www.google.co.uk"));
   }

   @Test
   void givenUrl_whenGetDomainNameRegex_thenReturnDomainName() {
      assertEquals("google.com", domainNameClient.getDomainName("www.google.com"));
      assertEquals("google.co.uk", domainNameClient.getDomainName("www.google.co.uk"));
      assertEquals("jira.tuyucheng.com", domainNameClient.getDomainName("jira.tuyucheng.com"));
      assertEquals("tuyucheng.com", domainNameClient.getDomainName("www.tuyucheng.com/test"));
   }

}
