package cn.tuyucheng.taketoday.passwordhashing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by PhysicsSam on 06-Sep-18.
 */
public class SHA512HasherUnitTest {

   private SHA512Hasher hasher;
   private SecureRandom secureRandom;

   @BeforeEach
   public void setUp() throws Exception {
      hasher = new SHA512Hasher();
      secureRandom = new SecureRandom();
   }

   @Test
   public void givenSamePasswordAndSalt_whenHashed_checkResultingHashesAreEqual() throws Exception {

      byte[] salt = new byte[16];
      secureRandom.nextBytes(salt);

      String hash1 = hasher.hash("password", salt);
      String hash2 = hasher.hash("password", salt);

      assertEquals(hash1, hash2);

   }

   @Test
   public void givenSamePasswordAndDifferentSalt_whenHashed_checkResultingHashesNotEqual() throws Exception {

      byte[] salt = new byte[16];
      secureRandom.nextBytes(salt);
      String hash1 = hasher.hash("password", salt);
      // generate a second salt
      byte[] secondSalt = new byte[16];
      String hash2 = hasher.hash("password", secondSalt);

      assertNotEquals(hash1, hash2);

   }

   @Test
   public void givenPredefinedHash_whenCorrectAttemptGiven_checkAuthenticationSucceeds() throws Exception {
      byte[] salt = new byte[16];
      secureRandom.nextBytes(salt);

      String originalHash = hasher.hash("password123", salt);

      assertTrue(hasher.checkPassword(originalHash, "password123", salt));
   }

   @Test
   public void givenPredefinedHash_whenIncorrectAttemptGiven_checkAuthenticationFails() throws Exception {
      byte[] salt = new byte[16];
      secureRandom.nextBytes(salt);

      String originalHash = hasher.hash("password123", salt);

      assertFalse(hasher.checkPassword(originalHash, "password124", salt));
   }
}