package gov.usdot.cv.service.rest;

import static org.junit.Assert.*;

import org.junit.Test;

public class CryptoHelperTest {

	@Test
	public void test() throws Exception {
		final String text = "some text to encrypt/decrypt";
		final String etext = CryptoHelper.encrypt(text);
		final String dtext = CryptoHelper.decrypt(etext);
		assertEquals(text, dtext);
	}
	
}
