package cn.tuyucheng.taketoday.spring.cloud.azure.keyvault;

import cn.tuyucheng.taketoday.spring.cloud.azure.keyvault.service.KeyVaultAutoconfiguredClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
class KeyVaultAutoconfiguredClientIntegrationTest {

   @Autowired
   @Qualifier(value = "KeyVaultAutoconfiguredClient")
   private KeyVaultAutoconfiguredClient keyVaultAutoconfiguredClient;

   @Test
   void whenANotExistingKeyIsProvided_thenShouldReturnAnError() {
      String secretKey = "mySecret";
      assertThrows(NoSuchElementException.class, () -> keyVaultAutoconfiguredClient.getSecret(secretKey));
   }
}