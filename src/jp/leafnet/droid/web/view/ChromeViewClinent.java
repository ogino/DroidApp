package jp.leafnet.droid.web.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ChromeViewClinent extends WebViewClient {

	private Activity context;
	private static final int MAX  = 10000;
	private int progress;
	
	public ChromeViewClinent(final Activity context) {
		super();
		this.context = context;
	}

	public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		this.context.setProgressBarVisibility(true);
		this.progress = 0;
	}

	public void onLoadResource(final WebView view, final String url) {
		super.onLoadResource(view, url);
		this.progress += MAX / 20;
		this.context.setProgress(this.progress);
	}

	public void onPageFinished(final WebView view, final String url) {
		super.onPageFinished(view, url);
		this.context.setProgress(MAX);
	}
}
