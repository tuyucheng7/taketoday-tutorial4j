package cn.tuyucheng.taketoday.crypto.exception;

import cn.tuyucheng.taketoday.crypto.utils.CryptoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public class InvalidKeyExamplesUnitTest {

   private SecretKey key;
   private String plainText;
   private byte[] cipherTextBytes;

   @BeforeEach
   public void before() throws GeneralSecurityException {
      key = CryptoUtils.getFixedKey();

      byte[] ivBytes = new byte[]{'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g', 'I', 's', 'G', 'r', 'e', 'a', 't', '!'};
      IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

      plainText = "https://www.tuyucheng.com/";
      byte[] plainTextBytes = plainText.getBytes();

      cipherTextBytes = cipher.doFinal(plainTextBytes);
   }

   @Test
   public void givenTextEncryptedWithCBC_whenDecryptingWithNoIv_thenInvalidKeyExceptionIsThrown() {
      Assertions.assertThrows(InvalidKeyException.class,
            () -> InvalidKeyExamples.decryptUsingCBCWithNoIV(key, cipherTextBytes));
   }

   @Test
   public void givenTextEncryptedWithCBC_whenDecryptingWithIv_thenTextIsDecrypted()
         throws InvalidKeyException, GeneralSecurityException {
      byte[] decryptedBytes = InvalidKeyExamples.decryptUsingCBCWithIV(key, cipherTextBytes);

      Assertions.assertEquals(plainText, new String(decryptedBytes));
   }

   @Test
   public void whenKeyIsTooShort_thenInvalidKeyExceptionIsThrown() {
      Assertions.assertThrows(InvalidKeyException.class, () -> InvalidKeyExamples.encryptWithKeyTooShort());
   }

   @Test
   public void whenKeyIsTooLong_thenInvalidKeyExceptionIsThrown() {
      Assertions.assertThrows(InvalidKeyException.class, () -> InvalidKeyExamples.encryptWithKeyTooLong());
   }

}
