package jp.leafnet.droid.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.leafnet.droid.R;
import jp.leafnet.droid.dialog.factory.ProgressDialogFactory;
import jp.leafnet.droid.web.Chrome;
import jp.leafnet.droid.xml.RSSParser;
import jp.leafnet.droid.xml.rss.Channel;
import jp.leafnet.droid.xml.rss.Item;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HeadLine extends Activity implements OnClickListener {

	private RSSParser parser;
	private Channel channel;
	private Integer index;
	private List<String> headUrlList;
	private List<String> linkUrlList;
	private ProgressDialog dialog;
	private Boolean loaded;
	private Logger logger;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.headline);
		this.createBasis();
	}

	private void createBasis() {
		this.parser = new RSSParser();
		this.headUrlList = new ArrayList<String>();
		this.linkUrlList =  new ArrayList<String>();
		this.createMenuScroll();
		this.createDialog();
		this.loaded = false;
		this.logger =  Logger.getLogger(HeadLine.class.getPackage().getName());
		this.logger.setLevel(Level.INFO);
	}
	
	private void createDialog() {
		this.dialog = ProgressDialogFactory.getSpinnerInstance(this, "Now Loading...");
	}

	private void createMenuScroll() {
		LinearLayout menuLayout = (LinearLayout)findViewById(R.id.MenuLayout);
		ResourceBundle bundle = ResourceBundle.getBundle("headline");
		Integer total = Integer.decode((String) bundle.getObject("news.site.total"));
		for (Integer i = 0; i< total; i++) {
			menuLayout.addView(createButton(bundle, i));
			this.headUrlList.add(bundle.getString("news.site" + i + ".url"));
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
		if (!this.loaded) {
			this.createHeadLines();
			this.loaded = true;
		}
	}

	private void createHeadLines() {
		this.createDialog();
		this.dialog.show();
		this.linkUrlList.clear();
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				channel = parser.createChannel(headUrlList.get(index));
				handler.sendMessage(createMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getLocalizedMessage());
			} catch (XmlPullParserException e) {
				logger.log(Level.SEVERE, e.getLocalizedMessage());
			} finally {
				dialog.dismiss();
			}
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
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			synchronized (channel) {
				createTitle(((Map<String, Object>)channel.getInside("title")).get("text").toString());
				createTable(channel.getItems());
			}
		}
	};
	
	private void createTitle(String title) {
		TextView textView = (TextView)findViewById(R.id.RSSTitle);
		textView.setText(title);
	}

	private final Integer ROWID_BEGIN = 9999;

	@SuppressWarnings("unchecked")
	private void createTable(final List<Item> itemList) {
		TableLayout layout = (TableLayout)findViewById(R.id.HeadLineTable);
		layout.removeAllViews();
		Integer id = ROWID_BEGIN + 1;
		for (Item item : itemList) {
			layout.addView(createTableRow(item, id++));
			this.linkUrlList.add(((Map<String, Object>)item.getInside("link")).get("text").toString());
		}
	}

	private TableRow createTableRow(final Item item, final Integer id) {
		TableRow row = new TableRow(this);
		row.setBackgroundColor(Color.LTGRAY);
		LinearLayout layout = createInnerLayout(item);
		row.addView(layout, createRowLayout(10, 1));
		row.setId(id);
		row.setOnClickListener(this);
		return row;
	}

	private LinearLayout createInnerLayout(Item item) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(createTitleView(item));
		layout.addView(createDescriptView(item));
		return layout;
	}

	private LayoutParams createRowLayout(int span, int margin) {
		TableRow.LayoutParams rowLayout = new TableRow.LayoutParams();
        rowLayout.span = span;
        rowLayout.bottomMargin = margin;
		return rowLayout;
	}

	@SuppressWarnings("unchecked")
	private TextView createDescriptView(Item item) {
		TextView descriptView = new TextView(this);
		descriptView.setBackgroundColor(Color.WHITE);
		descriptView.setTextSize(10);
		descriptView.setText(((Map<String, Object>)item.getInside("description")).get("text").toString());
		return descriptView;
	}

	@SuppressWarnings("unchecked")
	private TextView createTitleView(Item item) {
		TextView titleView = new TextView(this);
		titleView.setBackgroundColor(Color.WHITE);
		titleView.setTypeface(Typeface.DEFAULT_BOLD);
		titleView.setText(((Map<String, Object>)item.getInside("title")).get("text").toString());
		return titleView;
	}

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
		Integer id = v.getId();
		if (id > ROWID_BEGIN) {
			id = id % (ROWID_BEGIN + 1);
			Intent intent = new Intent(this, Chrome.class);
			intent.putExtra("URL", linkUrlList.get(id));
			this.startActivity(intent);
		} else {
			this.index = id;
			this.createHeadLines();
		}
	}

	private final static int FINISH_ID = 0;
	private final static int REFRESH_ID = 1;
	private final static int ABOUT_ID = 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem finishItem = menu.add(Menu.NONE, FINISH_ID, Menu.NONE, "終了");
		finishItem.setIcon(android.R.drawable.ic_lock_power_off);
		MenuItem refreshIcon = menu.add(Menu.NONE, REFRESH_ID, Menu.NONE, "更新");
		refreshIcon.setIcon(R.drawable.ic_menu_refresh);
		MenuItem aboutItem = menu.add(Menu.NONE, ABOUT_ID, Menu.NONE, "アプリについて");
		aboutItem.setIcon(android.R.drawable.ic_menu_info_details);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case FINISH_ID:
			this.finish();
			break;
		case REFRESH_ID:
			this.createHeadLines();
			break;
		case ABOUT_ID:
			this.showAbout();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAbout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("" +
				"このアプリケーションについて");
		builder.setMessage("Copyright (C) 2010 LeafNet Co.,Ltd. All Rights Reserved.");
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(RESULT_OK);
			}
		});
		builder.setCancelable(true);
		builder.create().show();
	}
}
