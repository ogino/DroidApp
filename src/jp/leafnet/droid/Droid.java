package jp.leafnet.droid;

import java.util.ResourceBundle;

import jp.leafnet.droid.map.GoogleMap;
import jp.leafnet.droid.news.HeadLine;
import jp.leafnet.droid.web.Chrome;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class Droid extends Activity {
	private Boolean daytime = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
	}

	@Override
	public void onStart() {
		super.onStart();
		TextView textView = (TextView)findViewById(R.id.DispText);
		textView.setText(this.createText());
	}

//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	public void showAlert(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("サンプルダイアログ");
		builder.setMessage("単なるテストです。\nそこまでしかない代物ですｗ");
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(RESULT_OK);
			}
		});
		builder.setCancelable(true);
		builder.create().show();
	}

	public void showWebView(View v) {
		Intent intent = new Intent(this, Chrome.class);
		intent.putExtra("URL", "http://blog.livedoor.jp/dqnplus/lite/");
		this.startActivity(intent);
	}

	public void showMapView(View v) {
		Intent intent = new Intent(this, GoogleMap.class);
		this.startActivity(intent);
	}

	public void swapText(View v) {
		TextView textView = (TextView)findViewById(R.id.DispText);
		textView.clearComposingText();
		textView.setText(this.createText());
	}

	private String createText() {
		String text = null;
		ResourceBundle bundle = ResourceBundle.getBundle("droid");
		if (this.daytime) text = bundle.getString("message.evening");
		else text = bundle.getString("message.morning");
		this.daytime = !this.daytime;
		return text;
	}

	public void showNewsView(View v) {
		Intent intent = new Intent(this, HeadLine.class);
		this.startActivity(intent);
	}

	public void showAboutView(View v) {
		
	}
}