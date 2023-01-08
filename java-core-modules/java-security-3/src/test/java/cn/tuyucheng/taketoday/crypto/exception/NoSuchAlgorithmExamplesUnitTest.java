package cn.tuyucheng.taketoday.crypto.exception;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class NoSuchAlgorithmExamplesUnitTest {

	@Test
	public void whenInitingCipherWithUnknownAlgorithm_thenNoSuchAlgorithmExceptionIsThrown()
		throws GeneralSecurityException {
		Assert.assertThrows(NoSuchAlgorithmException.class,
			() -> NoSuchAlgorithmExamples.getCipherInstanceWithBadAlgorithm());
	}

	@Test
	public void whenInitingCipherWithUnknownAlgorithmMode_thenNoSuchAlgorithmExceptionIsThrown()
		throws GeneralSecurityException {
		Assert.assertThrows(NoSuchAlgorithmException.class,
			() -> NoSuchAlgorithmExamples.getCipherInstanceWithBadAlgorithmMode());
	}

	@Test
	public void whenInitingCipherWithUnknownPadding_thenNoSuchAlgorithmExceptionIsThrown()
		throws GeneralSecurityException {
		Assert.assertThrows(NoSuchAlgorithmException.class,
			() -> NoSuchAlgorithmExamples.getCipherInstanceWithBadPadding());
	}

	@Test
	public void whenInitingCipherWithValidAlgorithm_thenCipherInstanceIsReturned() throws GeneralSecurityException {
		Assert.assertTrue(NoSuchAlgorithmExamples.getCipherInstanceWithValidAlgorithm() instanceof Cipher);
	}
}
