package cript;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// #if BASE
public class Cryptography {

	private String imei = "123456789"; // source

	public static void main(String[] args) {
		Cryptography c = new Cryptography();
		c.encodeIMEI();
	}

	private void encodeIMEI() {
		try {
			String encodedIMEI = Cryptography.getHash(this.imei);
			// #if LOG
			Log.i(encodedIMEI); // sink
			// #endif
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static String getHash(String imei)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		String input = digest.digest(imei.getBytes("UTF-8")).toString();
		return input;
	}
}
// #endif
