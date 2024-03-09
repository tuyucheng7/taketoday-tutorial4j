package cn.tuyucheng.taketoday.passwordhashing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PBKDF2HasherUnitTest {

   private PBKDF2Hasher mPBKDF2Hasher;

   @BeforeEach
   public void setUp() throws Exception {
      mPBKDF2Hasher = new PBKDF2Hasher();
   }

   @Test
   public void givenCorrectMessageAndHash_whenAuthenticated_checkAuthenticationSucceeds() throws Exception {
      String message1 = "password123";

      String hash1 = mPBKDF2Hasher.hash(message1.toCharArray());

      assertTrue(mPBKDF2Hasher.checkPassword(message1.toCharArray(), hash1));

   }

   @Test
   public void givenWrongMessage_whenAuthenticated_checkAuthenticationFails() throws Exception {
      String message1 = "password123";

      String hash1 = mPBKDF2Hasher.hash(message1.toCharArray());

      String wrongPasswordAttempt = "IamWrong";

      assertFalse(mPBKDF2Hasher.checkPassword(wrongPasswordAttempt.toCharArray(), hash1));

   }


}