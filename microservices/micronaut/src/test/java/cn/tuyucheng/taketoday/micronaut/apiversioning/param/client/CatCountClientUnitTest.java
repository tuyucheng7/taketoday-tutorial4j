package cn.tuyucheng.taketoday.micronaut.apiversioning.param.client;

import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class CatCountClientUnitTest {

   @Inject
   private EmbeddedApplication<?> application;

   @Inject
   private CatCountClient client;

   @Test
   void givenTheCountApi_whenUsingV1ViaParameterStrategy_shouldRouteToProperHandler() {
      assertEquals(10, client.count(null, 1).blockingSingle().split(",").length);
      assertEquals(5, client.count(5, 1).blockingSingle().split(",").length);
   }

   @Test
   void givenTheCountApi_whenUsingV2ViaParameterStrategy_shouldRouteToProperHandler() {
      assertThrows(HttpClientResponseException.class,
            () -> client.count(null, 2).count().blockingGet());

      assertEquals(6, client.count(6, 2).blockingSingle().split(",").length);
   }

   @Test
   void givenTheCountApi_whenUsingDefaultVersionViaParameterStrategy_shouldRouteToProperHandler() {
      assertEquals(10, client.count(null, null).blockingSingle().split(",").length);
      assertEquals(6, client.count(6, null).blockingSingle().split(",").length);
   }
}