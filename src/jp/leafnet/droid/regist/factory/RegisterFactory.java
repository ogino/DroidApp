package jp.leafnet.droid.regist.factory;

import android.content.Context;
import android.os.Handler;
import jp.leafnet.droid.regist.Register;
import jp.leafnet.droid.regist.impl.instapaper.Instapaper;
import jp.leafnet.droid.regist.impl.readitlater.ReadItLater;

public class RegisterFactory {

	private RegisterFactory() {
	}
	
	public static Register getInstance(final Context context, final String url, final Handler handler, final Boolean apiKeyUse) {
		if (apiKeyUse) return new ReadItLater(context, url, handler);
		return new Instapaper(context, url, handler);
	}
}
