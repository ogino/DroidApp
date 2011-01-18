package jp.leafnet.droid.web;

import java.lang.reflect.Field;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.leafnet.droid.R;
import jp.leafnet.droid.news.HeadLine;
import jp.leafnet.droid.news.conf.UserPrefActivity;
import jp.leafnet.droid.web.view.ChromeViewClinent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
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
	private final static int PREP_ID = 3;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem backItem = menu.add(Menu.NONE, BACK_ID, Menu.NONE, R.string.back);
		MenuItem fwdItem = menu.add(Menu.NONE, FWD_ID, Menu.NONE, R.string.forward);
		MenuItem instaItem = menu.add(Menu.NONE, INSTA_ID, Menu.NONE, R.string.instapaper);
		MenuItem prepItem = menu.add(Menu.NONE, PREP_ID, Menu.NONE, R.string.preference);
		backItem.setIcon(R.drawable.ic_menu_back);
		fwdItem.setIcon(R.drawable.ic_menu_forward);
		instaItem.setIcon(android.R.drawable.ic_menu_myplaces);
		prepItem.setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }

	private final static String NO_USERNAME = "Username not found...";
	private final static String NO_PASSWORD = "Password not found...";

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
			this.sendInstapaper();
			break;
		case PREP_ID:
			this.showPreference();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sendInstapaper() {
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(this);
		String username = userPreference.getString(this.getResources().getString(R.string.insta_username), NO_USERNAME);
		String password = userPreference.getString(this.getResources().getString(R.string.insta_password), NO_PASSWORD);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("https://www.instapaper.com/api/add");
		String title = "Success:";
		String message = "Registered it on your account!";
		MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart("username", new StringBody(username));
			entity.addPart("password", new StringBody(password));
			entity.addPart("url", new StringBody(this.webView.getUrl()));
			httpPost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_CREATED) {
				title = "Error";
				message = "Error: not registered. code is " + statusCode.toString();
			}
			this.showDialog(title, message);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		}
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

	private void showPreference() {
		Intent intent = new Intent(this, UserPrefActivity.class);
		this.startActivity(intent);
	}
}
