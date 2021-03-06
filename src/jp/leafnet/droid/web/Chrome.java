package jp.leafnet.droid.web;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.leafnet.droid.R;
import jp.leafnet.droid.dialog.factory.ProgressDialogFactory;
import jp.leafnet.droid.news.HeadLine;
import jp.leafnet.droid.news.conf.UserPrefActivity;
import jp.leafnet.droid.regist.Register;
import jp.leafnet.droid.regist.factory.RegisterFactory;
import jp.leafnet.droid.twitter.Twitter;
import jp.leafnet.droid.twitter.bitly.Bitly;
import jp.leafnet.droid.web.view.ChromeViewClinent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Chrome extends Activity {

	private WebView webView;
	private Logger logger = Logger.getLogger(HeadLine.class.getPackage().getName());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configureWindow();
		this.logger.setLevel(Level.INFO);
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
		this.webView.setFocusable(true);
		this.webView.setFocusableInTouchMode(true);
		this.webView.loadUrl(getIntent().getStringExtra("URL"));
		this.webView.setWebViewClient(new ChromeViewClinent(this));
		this.createWebSettings();
	}

	private void createWebSettings() {
		WebSettings settings = this.webView.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setUseWideViewPort(true);
		settings.setUserAgentString(this.createUserAgent());
		settings.setJavaScriptEnabled(true);
		settings.setSaveFormData(false);
		settings.setSavePassword(false);
		try {
			Field nameField = settings.getClass().getDeclaredField("mBuiltInZoomControls");
			nameField.setAccessible(true);
			nameField.set(settings, false);
		} catch (Exception e) {
			this.logger.log(Level.SEVERE, e.getLocalizedMessage());
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
		this.webView.clearSslPreferences();
		CookieManager manager = CookieManager.getInstance();
		manager.removeAllCookie();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private final static int BACK_ID = 0;
	private final static int FWD_ID = 1;
	private final static int INSTA_ID = 2;
	private final static int READIT_ID = 3;
	private final static int SHARE_ID = 4;
	private final static int TWEET_ID = 5;
	private final static int PREP_ID = 6;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem backItem = menu.add(Menu.NONE, BACK_ID, Menu.NONE, R.string.back);
		MenuItem fwdItem = menu.add(Menu.NONE, FWD_ID, Menu.NONE, R.string.forward);
		MenuItem instaItem = menu.add(Menu.NONE, INSTA_ID, Menu.NONE, R.string.instapaper);
		MenuItem readItem = menu.add(Menu.NONE, READIT_ID, Menu.NONE, R.string.readitlater);
		MenuItem shareItem = menu.add(Menu.NONE, SHARE_ID, Menu.NONE, R.string.share);
		MenuItem tweetItem = menu.add(Menu.NONE, TWEET_ID, Menu.NONE, R.string.twitter);
		MenuItem prepItem = menu.add(Menu.NONE, PREP_ID, Menu.NONE, R.string.preference);
		backItem.setIcon(R.drawable.ic_menu_back);
		fwdItem.setIcon(R.drawable.ic_menu_forward);
		instaItem.setIcon(R.drawable.instapeper);
		readItem.setIcon(R.drawable.readitlater);
		shareItem.setIcon(android.R.drawable.ic_menu_share);
		tweetItem.setIcon(R.drawable.twitter);
		prepItem.setIcon(android.R.drawable.ic_menu_preferences);
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
		case INSTA_ID:
			this.sendRegister(false);
			break;
		case READIT_ID:
			this.sendRegister(true);
			break;
		case SHARE_ID:
			this.sendShareApp();
			break;
		case TWEET_ID:
			this.webView.loadUrl(this.createTweetUrl());
			break;
		case PREP_ID:
			this.showPreference();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private static final String HASH_TAG = "#leafnewsreader";
	private String createTweetUrl() {
		Twitter twitter = new Twitter(this.webView.getTitle(), this.webView.getUrl(), HASH_TAG);
		return twitter.createTweetUrl();
	}

	private Register register;
	private ProgressDialog dialog;

	private void sendRegister(final Boolean apiKeyUse) {
		this.dialog = ProgressDialogFactory.getSpinnerInstance(this, this.getString(R.string.loading));
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				synchronized (register) {
					Map<String, String> resultMap = register.getResultMap();
					showDialog(resultMap.get("title"), resultMap.get("message"));
					dialog.dismiss();
				}
			}
		};
		this.dialog.show();
		this.register = RegisterFactory.getInstance(this, this.webView.getUrl(), handler, apiKeyUse);
		Thread thread = new Thread(this.register);
		thread.start();
	}

	private void showDialog(final String title, final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(RESULT_OK);
			}
		});
		builder.setCancelable(true);
		builder.create().show();
	}

	private static final String TWEET_FORMAT = "%s %s %s";

	private void sendShareApp() {
		Bitly bitly = new Bitly();
		String url = bitly.createUrl(this.webView.getUrl());
		String sendText = String.format(TWEET_FORMAT, this.webView.getTitle(), url, HASH_TAG);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, sendText);
		this.startActivity(intent);
	}

	private void showPreference() {
		Intent intent = new Intent(this, UserPrefActivity.class);
		this.startActivity(intent);
	}
}
