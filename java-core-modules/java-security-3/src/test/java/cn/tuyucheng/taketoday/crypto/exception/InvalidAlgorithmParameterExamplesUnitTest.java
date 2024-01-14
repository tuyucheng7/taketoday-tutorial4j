package cn.tuyucheng.taketoday.crypto.exception;

import cn.tuyucheng.taketoday.crypto.utils.CryptoUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

public class InvalidAlgorithmParameterExamplesUnitTest {

   private SecretKey key;
   private String plainText;

   @Before
   public void before() throws GeneralSecurityException {
      key = CryptoUtils.getFixedKey();

      plainText = "https://www.tuyucheng.com/";
   }

   @Test
   public void givenIvIsTooShort_whenEncryptingUsingCBC_thenInvalidAlgorithmParameterExceptionIsThrown()
         throws GeneralSecurityException {
      byte[] ivBytes = new byte[]{'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g', 'I', 's', 'G', 'r', 'e', 'a', 't'};
      Assert.assertThrows(InvalidAlgorithmParameterException.class,
            () -> InvalidAlgorithmParameterExamples.encryptUsingIv(key, ivBytes, plainText));
   }

   @Test
   public void givenIvIsTooLong_whenEncryptingUsingCBC_thenInvalidAlgorithmParameterExceptionIsThrown()
         throws GeneralSecurityException {
      byte[] ivBytes = new byte[]{'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g', 'I', 's', 'G', 'r', 'e', 'a', 't', '!',
            '?'};
      Assert.assertThrows(InvalidAlgorithmParameterException.class,
            () -> InvalidAlgorithmParameterExamples.encryptUsingIv(key, ivBytes, plainText));
   }

   @Test
   public void givenIvIsCorrectSize_whenEncryptingUsingCBC_thenNoExceptionIsThrown() throws GeneralSecurityException {
      byte[] ivBytes = new byte[]{'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g', 'I', 's', 'G', 'r', 'e', 'a', 't', '!'};
      byte[] cipherTextBytes = InvalidAlgorithmParameterExamples.encryptUsingIv(key, ivBytes, plainText);

      IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

      byte[] decryptedBytes = cipher.doFinal(cipherTextBytes);

      Assert.assertEquals(plainText, new String(decryptedBytes));
   }
}
