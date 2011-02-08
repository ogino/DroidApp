package jp.leafnet.droid.regist.impl.readitlater;

import android.content.Context;
import android.os.Handler;
import jp.leafnet.droid.R;
import jp.leafnet.droid.regist.impl.RegisterImpl;

public class ReadItLater extends RegisterImpl {

	public ReadItLater(Context context, String url, Handler handler) {
		super(context, url, handler);
	}

	@Override
	public void run() {
		this.apiRoot = "https://readitlaterlist.com/v2/add";
		this.apiKey = "d00A0m6fd4908K40x4p6dS2p89T7La78";
		this.createUserInfo(R.string.readit_username, R.string.readit_password);
		super.run();
	}
}
