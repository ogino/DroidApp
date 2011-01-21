package jp.leafnet.droid.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;

public class CryptoUtil {

	private static final byte[] KEY_BYTES = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
	private static final String ALGORISM = "AES/ECB/PKCS5Padding";
	private final static String SECRET_KEY = "secret.key";
	private static Logger logger = Logger.getLogger(CryptoUtil.class.getPackage().getName());

	public static String encodeString(final String source, final SecretKeySpec keySpec) {
		logger.setLevel(Level.WARNING);
		String encoded = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORISM);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			encoded = StringUtil.encodeBase64(cipher.doFinal(source.getBytes()));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "CryptoUtil#encodeString exception: " +e.getLocalizedMessage());
		}
        return encoded;
	}

	public static String decodeString(final String source, final SecretKeySpec keySpec) {
		logger.setLevel(Level.WARNING);
		String decoded = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORISM);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			decoded = new String(cipher.doFinal(StringUtil.decodeBase64(source)));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "CryptoUtil#decodeString exception: " + e.getLocalizedMessage());
		}
        return decoded;
	}

	public static SecretKeySpec createKeySpec(final Context context) {
		SecretKeySpec keySpec = readKeySpec(context);
		if (keySpec == null) keySpec = generateKeySpec(context);
		return keySpec;
	}

	private static SecretKeySpec readKeySpec(final Context context) {
		SecretKeySpec keySpec = null;
		try {
			FileInputStream fileStream = context.openFileInput(SECRET_KEY);
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			keySpec = (SecretKeySpec) objectStream.readObject();
		} catch (Exception e) {
			logger.log(Level.INFO, "CryptoUtil#readKeySpec exception: " + e.getLocalizedMessage());
		}
		return keySpec;
	}

	private static SecretKeySpec generateKeySpec(final Context context) {
		SecretKeySpec keySpec = null;
		try {
			keySpec = new SecretKeySpec(KEY_BYTES, 0, 16, "AES");
			FileOutputStream fileStream = context.openFileOutput(SECRET_KEY, Context.MODE_PRIVATE);
			new FileOutputStream("secret.key", false);
			ObjectOutputStream objectStrem =  new ObjectOutputStream(fileStream);
			objectStrem.writeObject(keySpec);
			objectStrem.close();
			fileStream.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "CryptoUtil#createKeyPair exception: " +e.getLocalizedMessage());
		}
		return keySpec;
	}
}
