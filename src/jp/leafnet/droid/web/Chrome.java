package jp.leafnet.droid.web;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Chrome extends Activity {

	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.createWebView();
		this.setContentView(this.webView);
	}

	private void createWebView() {
		this.webView = new WebView(this);
		this.webView.setAlwaysDrawnWithCacheEnabled(false);
		this.webView.loadUrl(getIntent().getStringExtra("URL"));
		this.webView.setWebViewClient(new WebViewClient() {});
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.webView.clearHistory();
		this.webView.clearCache(true);
		this.webView.clearView();
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
