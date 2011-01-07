package jp.leafnet.droid.web;

import jp.leafnet.droid.dialog.factory.ProgressDialogFactory;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Chrome extends Activity {

	private WebView webView;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.createDialog();
		this.createWebView();
		this.setContentView(this.webView);
	}
	
	private void createDialog() {
		this.dialog = ProgressDialogFactory.getSpinnerInstance(this, "Now Loading...");
	}

	private void createWebView() {
		this.webView = new WebView(this);
		this.webView.setAlwaysDrawnWithCacheEnabled(false);
		this.webView.loadUrl(getIntent().getStringExtra("URL"));
		this.webView.setWebViewClient(new WebViewClient() {});
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.dialog.show();
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			webView.loadUrl(getIntent().getStringExtra("URL"));
			handler.sendMessage(createMessage());
		}
	};

	private Message createMessage() {
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("", "");
		message.setData(bundle);
		return message;
	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			dialog.dismiss();
		}
	};

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
