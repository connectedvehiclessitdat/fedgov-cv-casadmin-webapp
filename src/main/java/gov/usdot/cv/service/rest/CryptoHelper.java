package gov.usdot.cv.service.rest;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import gov.usdot.cv.resources.PrivateResourceLoader;

public class CryptoHelper {
	
	private static final String algorithm = "AES";
	// Maximum key length supported by JCE in the default installation is 128 bits.
	// To use a longer key we would need to install Java Cryptography Extension (JCE) 
	// Unlimited Strength Jurisdiction Policy jar from Oracle or use BouncyCastle 
	private static final int keyLength = 128;
	private static final int iterationCount = 65536;
	private static final String keyBase =
				PrivateResourceLoader.getProperty("@casadmin-webapp/crypto.helper.key.base@");
	private static final String keySalt =
				PrivateResourceLoader.getProperty("@casadmin-webapp/crypto.helper.key.salt@");
	
	private static final Key key = generateKey();
	
	private static Key generateKey() {
		try {
			final char[] password = keyBase.toCharArray();
			final byte[] salt = keySalt.getBytes(StandardCharsets.UTF_8);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password, salt, iterationCount, keyLength);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), algorithm);
			return secret;
		} catch (Exception ex) {
	        throw new RuntimeException("Failed to initialize secret key. Reason: " + ex.getMessage(), ex);
		}
	}

	static String decrypt(String encryptedText) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher chiper = Cipher.getInstance(algorithm);
		chiper.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = Base64.decodeBase64(encryptedText);
		byte[] decValue = chiper.doFinal(decordedValue);
		String decryptedValue = new String(decValue, StandardCharsets.UTF_8);
		return decryptedValue;
	}

	static String encrypt(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher chiper = Cipher.getInstance(algorithm);
		chiper.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = chiper.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		String encryptedValue = Base64.encodeBase64String(encVal);
		return encryptedValue;
	}

}
