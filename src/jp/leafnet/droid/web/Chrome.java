package jp.leafnet.droid.web;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

import jp.leafnet.droid.R;
import jp.leafnet.droid.web.view.ChromeViewClinent;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Chrome extends Activity {

	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configureWindow();
		this.createWebView();
		this.setContentView(this.webView);
		this.createTitle();
	}

	private void createTitle() {
		 this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.webtitle);
	}

	private void configureWindow() {
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	private void createWebView() {
		this.webView = new WebView(this);
		this.webView.setAlwaysDrawnWithCacheEnabled(false);
		this.webView.loadUrl(getIntent().getStringExtra("URL"));
		this.webView.setWebViewClient(new ChromeViewClinent(this));
		this.createWebSettings();
	}

	private void createWebSettings() {
		WebSettings settings = this.webView.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setUserAgentString(this.createUserAgent());
		try {
		    Field nameField = settings.getClass().getDeclaredField("mBuiltInZoomControls");
		    nameField.setAccessible(true);
		    nameField.set(settings, false);
		} catch (Exception e) {
		    e.printStackTrace();
		    settings.setBuiltInZoomControls(false);
		}
	}

	private String createUserAgent() {
		ResourceBundle bundle = ResourceBundle.getBundle("chrome");
		return bundle.getString("chrome.useragent");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			webView.loadUrl(getIntent().getStringExtra("URL"));
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		this.webView.clearHistory();
		this.webView.clearCache(true);
		this.webView.clearFormData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private final static int BACK_ID = 0;
	private final static int FWD_ID = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem backItem = menu.add(Menu.NONE, BACK_ID, Menu.NONE, "戻る");
		MenuItem fwdItem = menu.add(Menu.NONE, FWD_ID, Menu.NONE, "進む");
		backItem.setIcon(R.drawable.ic_menu_back);
		fwdItem.setIcon(R.drawable.ic_menu_forward);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case BACK_ID:
			this.webView.goBack();
			break;
		case FWD_ID:
			this.webView.goForward();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
