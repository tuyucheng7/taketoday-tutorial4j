package cn.tuyucheng.taketoday.crypto.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class NoSuchAlgorithmExamplesUnitTest {

   @Test
   public void whenInitingCipherWithUnknownAlgorithm_thenNoSuchAlgorithmExceptionIsThrown()
         throws GeneralSecurityException {
      Assertions.assertThrows(NoSuchAlgorithmException.class,
            () -> NoSuchAlgorithmExamples.getCipherInstanceWithBadAlgorithm());
   }

   @Test
   public void whenInitingCipherWithUnknownAlgorithmMode_thenNoSuchAlgorithmExceptionIsThrown()
         throws GeneralSecurityException {
      Assertions.assertThrows(NoSuchAlgorithmException.class,
            () -> NoSuchAlgorithmExamples.getCipherInstanceWithBadAlgorithmMode());
   }

   @Test
   public void whenInitingCipherWithUnknownPadding_thenNoSuchAlgorithmExceptionIsThrown()
         throws GeneralSecurityException {
      Assertions.assertThrows(NoSuchAlgorithmException.class,
            () -> NoSuchAlgorithmExamples.getCipherInstanceWithBadPadding());
   }

   @Test
   public void whenInitingCipherWithValidAlgorithm_thenCipherInstanceIsReturned() throws GeneralSecurityException {
      Assertions.assertTrue(NoSuchAlgorithmExamples.getCipherInstanceWithValidAlgorithm() instanceof Cipher);
   }
}
