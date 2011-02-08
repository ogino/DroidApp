package jp.leafnet.droid.regist.impl.instapaper;

import jp.leafnet.droid.R;
import jp.leafnet.droid.regist.impl.RegisterImpl;
import android.content.Context;
import android.os.Handler;

public class Instapaper extends RegisterImpl {

	public Instapaper(final Context context, final String url, final Handler handler) {
		super(context, url, handler);
	}

	@Override
	public void run() {
		this.apiRoot = "https://www.instapaper.com/api/add";
		this.createUserInfo(R.string.insta_username, R.string.insta_password);
		super.run();
	}
}
