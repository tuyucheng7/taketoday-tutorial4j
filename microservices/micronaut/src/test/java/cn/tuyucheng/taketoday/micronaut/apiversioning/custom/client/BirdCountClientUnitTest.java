package cn.tuyucheng.taketoday.micronaut.apiversioning.custom.client;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@Property(name = "my.router.versioning.enabled", value = "true")
class BirdCountClientUnitTest {

   @Inject
   private EmbeddedApplication<?> application;

   @Inject
   private BirdCountClient client;

   @Test
   void givenTheCountApi_whenUsingV1ViaCustomStrategy_shouldRouteToProperHandler() {
      assertEquals(10, client.countV1(null).blockingSingle().split(",").length);
      assertEquals(4, client.countV1(4).blockingSingle().split(",").length);
   }

   @Test
   void givenTheCountApi_whenUsingV2ViaCustomStrategy_shouldRouteToProperHandler() {
      assertThrows(HttpClientResponseException.class,
            () -> client.countV2(null).count().blockingGet());

      assertEquals(6, client.countV2(6).blockingSingle().split(",").length);
   }

   @Test
   void givenTheCountApi_whenUsingDefaultVersionViaCustomStrategy_shouldRouteToProperHandler() {
      assertThrows(HttpClientResponseException.class,
            () -> client.countDefault(null).count().blockingGet());

      assertEquals(6, client.countDefault(6).blockingSingle().split(",").length);
   }
}