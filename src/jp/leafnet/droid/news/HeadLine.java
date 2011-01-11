package jp.leafnet.droid.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import jp.leafnet.droid.R;
import jp.leafnet.droid.dialog.factory.ProgressDialogFactory;
import jp.leafnet.droid.web.Chrome;
import jp.leafnet.droid.xml.RSSParser;
import jp.leafnet.droid.xml.rss.Channel;
import jp.leafnet.droid.xml.rss.Item;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HeadLine extends Activity implements OnClickListener {

	private RSSParser parser;
	private Integer index;
	private List<String> menuUrls;
	private List<String> bodyUrls;
	private ProgressDialog dialog;
	private Boolean loaded;
	private Logger logger;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headline);
		this.createBasis();
	}

	private void createDialog() {
		this.dialog = ProgressDialogFactory.getSpinnerInstance(this, "Now Loading...");
	}

	private void createBasis() {
		this.parser = new RSSParser();
		this.menuUrls = new ArrayList<String>();
		this.bodyUrls =  new ArrayList<String>();
		this.createMenuScroll();
		this.loaded = false;
		this.logger =  Logger.getLogger("Exception");
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
		if (!this.loaded) {
			this.createHeadLines();
			this.loaded = true;
		}
	}

	private void createHeadLines() {
		this.createDialog();
		this.dialog.show();
		this.bodyUrls.clear();
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				parser.createChannel(menuUrls.get(index));
				handler.sendMessage(createMessage());
			} catch (SAXException e) {
				logger.log(Level.SEVERE, e.getLocalizedMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getLocalizedMessage());
			} catch (ParserConfigurationException e) {
				logger.log(Level.SEVERE, e.getLocalizedMessage());
			} catch (FactoryConfigurationError e) {
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
			Channel channel = parser.getHandler().getChannel();
			createTitle(((Map<String, Object>)channel.getInside("title")).get("text").toString());
			createTable(channel.getItems());
		}
	};
	
	private void createTitle(String title) {
		TextView textView = (TextView)findViewById(R.id.RSSTitle);
		textView.setText(title);
	}

	private final Integer ROWID_BEGIN = 9999;

	@SuppressWarnings("unchecked")
	private void createTable(List<Item> itemList) {
		TableLayout layout = (TableLayout)findViewById(R.id.HeadLineTable);
		layout.removeAllViews();
		Integer id = ROWID_BEGIN + 1;
		for (Item item : itemList) {
			layout.addView(createTableRow(item, id++));
			this.bodyUrls.add(((Map<String, Object>)item.getInside("link")).get("text").toString());
		}
	}

	private TableRow createTableRow(Item item, Integer id) {
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
			intent.putExtra("URL", bodyUrls.get(id));
			this.startActivity(intent);
		} else {
			this.index = id;
			this.createHeadLines();
		}
	}
}
