package jp.leafnet.droid.web;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Chrome extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(this.createWebView());
	}

	private WebView createWebView() {
		WebView webView = new WebView(this);
		webView.setAlwaysDrawnWithCacheEnabled(false);
		webView.loadUrl("http://blog.livedoor.jp/geek/");
		webView.setWebViewClient(new WebViewClient() {});
		return webView;
	}

//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
