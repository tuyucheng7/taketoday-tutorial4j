package cn.tuyucheng.taketoday.crypto.exception;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public class NoSuchAlgorithmExamples {

	public static Cipher getCipherInstanceWithBadAlgorithm()
		throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("ABC");
	}

	public static Cipher getCipherInstanceWithBadAlgorithmMode()
		throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("AES/ABC");
	}

	public static Cipher getCipherInstanceWithBadPadding()
		throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("AES/CBC/ABC");
	}

	public static Cipher getCipherInstanceWithValidAlgorithm()
		throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("AES/CBC/PKCS5Padding");
	}
}
