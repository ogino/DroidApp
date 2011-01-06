package jp.leafnet.droid.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import jp.leafnet.droid.R;
import jp.leafnet.droid.dialog.factory.ProgressDialogFactory;
import jp.leafnet.droid.xml.RSSParser;
import jp.leafnet.droid.xml.rss.Channel;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeadLine extends Activity implements OnClickListener {

	private RSSParser parser;
	private Integer index;
	private List<String> menuUrls;
	private List<String> bodyUrls;
	private ProgressDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createDialog();
		setContentView(R.layout.headline);
		this.createBasis();
	}

	private void createDialog() {
		this.dialog = ProgressDialogFactory.getSpinnerInstance(this, "Now Loading...");
	}

	private void createBasis() {
		this.parser = new RSSParser();
		this.menuUrls = new ArrayList<String>();
		this.createMenuScroll();
	}

	private void createMenuScroll() {
		LinearLayout menuLayout = (LinearLayout)findViewById(R.id.MenuLayout);
		ResourceBundle bundle = ResourceBundle.getBundle("headline");
		Integer total = Integer.decode((String) bundle.getObject("news.site.total"));
		for (Integer i = 0; i< total; i++) {
			menuLayout.addView(createButton(bundle, i));
			this.menuUrls.add(bundle.getString("news.site" + i + ".url"));
		}
		this.index = 0;
	}

	private Button createButton(ResourceBundle bundle, Integer i) {
		Button button = new Button(this);
		button.setTextColor(Color.RED);
		button.setBackgroundColor(Color.LTGRAY);
		button.setText(bundle.getString("news.site" + i + ".name"));
		button.setOnClickListener(this);
		button.setId(i);
		return button;
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.createHeadLines();
	}

	private void createHeadLines() {
		this.dialog.show();
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				parser.createChannel(menuUrls.get(index));
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("", "");
				message.setData(bundle);
				handler.sendMessage(message);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				e.printStackTrace();
			} finally {
				dialog.dismiss();
			}
		}
	};
	
	private final Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Channel channel = parser.getHandler().getChannel();
			String title = ((Map<String, Object>)channel.getInside("title")).get("text").toString();
			TextView textView = (TextView)findViewById(R.id.RSSTitle);
			textView.setText(title);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
//		Intent intent = new Intent(this, Chrome.class);
//		intent.putExtra("URL", menuUrls.get(v.getId()).toString());
//		this.startActivity(intent);
		this.index = v.getId();
		this.createHeadLines();
	}
}
