package jp.leafnet.droid.web;

import java.lang.reflect.Field;

import jp.leafnet.droid.dialog.factory.ProgressDialogFactory;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebSettings;
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
		createWebSettings();
	}

	private void createWebSettings() {
		WebSettings settings = this.webView.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.3; ja-jp; google_sdk Build/GRH55) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		try{
		    Field nameField = settings.getClass().getDeclaredField("mBuiltInZoomControls");
		    nameField.setAccessible(true);
		    nameField.set(settings, false);
		}catch(Exception e){
		    e.printStackTrace();
		    settings.setBuiltInZoomControls(false);
		}
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
