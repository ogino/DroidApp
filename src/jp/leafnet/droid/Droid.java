package jp.leafnet.droid;

import jp.leafnet.droid.map.GoogleMap;
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

	final private String EVENING_MESSAGE = "こんばんは。\n皆様お元気でしょうか？\n私は元気です！\n" +
	"そろそろおねむの時間ですね。\nおやすみなさいませ、ご主人様\n" +
	"EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING " +
	"EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING " +
	"EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING EVENING";
	final private String MORNING_MESSAGE = "おはようございます。\nご主人様\n今日も元気に参りましょう\n" +
	"あらあらまだ眠いですか？\n皆様お待ちですよ。\n" +
	"MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING " +
	"MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING " +
	"MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING MORNING";
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
		textView.setText(EVENING_MESSAGE);
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
		this.startActivity(intent);
	}

	public void showMapView(View v) {
		Intent intent = new Intent(this, GoogleMap.class);
		this.startActivity(intent);
	}

	public void swapText(View v) {
		TextView textView = (TextView)findViewById(R.id.DispText);
		textView.clearComposingText();
		if (this.daytime) textView.setText(EVENING_MESSAGE);
		else textView.setText(MORNING_MESSAGE);
		this.daytime = !this.daytime;
	}
}