package cn.tuyucheng.taketoday.crypto.exception;

import cn.tuyucheng.taketoday.crypto.utils.CryptoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;

public class BadPaddingExamplesUnitTest {

   private SecretKey key;
   private IvParameterSpec ivParameterSpec;
   private String plainText;
   private byte[] plainTextBytes;

   @BeforeEach
   public void before() throws GeneralSecurityException {
      key = CryptoUtils.getFixedKey();

      byte[] ivBytes = new byte[]{'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g', 'I', 's', 'G', 'r', 'e', 'a', 't', '!'};
      ivParameterSpec = new IvParameterSpec(ivBytes);

      plainText = "https://www.tuyucheng.com/";
      plainTextBytes = plainText.getBytes();
   }

   @Test
   public void givenTwoDifferentAlgorithmPaddings_whenDecrypting_thenBadPaddingExceptionIsThrown()
         throws GeneralSecurityException {
      Assertions.assertThrows(BadPaddingException.class,
            () -> BadPaddingExamples.encryptAndDecryptUsingDifferentPaddings(key, plainTextBytes));
   }

   @Test
   public void givenTwoDifferentKeys_whenDecrypting_thenBadPaddingExceptionIsThrown() throws GeneralSecurityException {
      Assertions.assertThrows(BadPaddingException.class,
            () -> BadPaddingExamples.encryptAndDecryptUsingDifferentKeys(plainTextBytes));
   }

   @Test
   public void givenTwoDifferentAlgorithms_whenDecrypting_thenBadPaddingExceptionIsThrown()
         throws GeneralSecurityException {
      Assertions.assertThrows(BadPaddingException.class, () -> BadPaddingExamples
            .encryptAndDecryptUsingDifferentAlgorithms(key, ivParameterSpec, plainTextBytes));
   }

   @Test
   public void givenSameVariablesUsedForEncryptingAndDecrypting_whenDecrypting_thenNoExceptionIsThrown()
         throws GeneralSecurityException {
      byte[] decryptedBytes = BadPaddingExamples.encryptAndDecryptUsingSamePaddingKeyAndAlgorithm(key,
            plainTextBytes);

      Assertions.assertEquals(plainText, new String(decryptedBytes));
   }
}
