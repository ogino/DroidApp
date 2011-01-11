package jp.leafnet.droid.util;

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
}
