package jp.leafnet.droid.web.view;

import jp.leafnet.droid.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

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
		TextView titleView = (TextView)this.context.findViewById(R.id.WebViewTitle);
		titleView.setBackgroundColor(Color.argb(0, 0, 0, 0));
		titleView.setText("Now Loading...");
		ProgressBar progressBar = (ProgressBar)this.context.findViewById(R.id.WebViewProgress);
		progressBar.setMax(MAX);
		progressBar.setVisibility(ProgressBar.VISIBLE);
		this.progress = 0;
	}

	public void onLoadResource(final WebView view, final String url) {
		super.onLoadResource(view, url);
		this.progress += MAX / 20;
		ProgressBar progressBar = (ProgressBar)this.context.findViewById(R.id.WebViewProgress);
		progressBar.setProgress(this.progress);
		progressBar.setSecondaryProgress(this.progress + (MAX / 20));
	}

	public void onPageFinished(final WebView view, final String url) {
		super.onPageFinished(view, url);
		ProgressBar progressBar = (ProgressBar)this.context.findViewById(R.id.WebViewProgress);
		progressBar.setProgress(MAX);
		progressBar.setVisibility(ProgressBar.GONE);
		TextView titleView = (TextView)this.context.findViewById(R.id.WebViewTitle);
		titleView.setText(view.getTitle());
	}
}
