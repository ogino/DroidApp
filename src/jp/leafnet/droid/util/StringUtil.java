package jp.leafnet.droid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.leafnet.droid.base64.Base64;

public class StringUtil {

	public static boolean isEmpty(final String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isEquals(final String source, final String destination) {
		if (source == null && destination == null) return true;
		else if (source == null && destination != null) return false;
		else if (source != null && destination == null) return false;
		return source.equals(destination);
	}

	public static String removeTags(final String source, final String regex) {
		String modified = new String(source);
		Pattern pattern =  Pattern.compile(regex);
		Matcher matcher = pattern.matcher(modified);
		return matcher.replaceAll("");
	}

	public static String removeTags(final String source) {
		String modified = removeTags(source, "<(\"[^\"]*\"|'[^']*'|[^'\">])*>");
		return replaceHTMLTags(modified);
	}

	private static String replaceHTMLTags(final String source) {
		String modified = new String(source);
		return modified.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"").replace("\n", "");
	}

	public static String encodeBase64(byte[] data) {
	  	return Base64.encodeBytes(data);
	}

	public static byte[] decodeBase64(String base64) throws Exception {
	    return Base64.decode(base64);
	}
}
