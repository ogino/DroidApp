package jp.leafnet.droid.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.leafnet.droid.util.StringUtil;
import jp.leafnet.droid.xml.rss.Channel;
import jp.leafnet.droid.xml.rss.Item;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class RSSParser {

	private Boolean inside = false;
	private Boolean beginParse = false;
	private Channel channel = null;
	private Item item = null;
	private String tag = null;

	public Channel createChannel(final String url) throws IOException, XmlPullParserException {
		XmlPullParser parser = Xml.newPullParser();
		Reader reader = createReader(url);
		Boolean done = false;
		parser.setInput(reader);
		Integer eventType = parser.getEventType();
		while (!done) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				this.startElement(parser);
				break;
			case XmlPullParser.END_TAG:
				this.teminateElement(parser);
				break;
			case XmlPullParser.TEXT:
				this.createText(parser);
				break;
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			default:
				break;
			}
			if (!done) eventType = parser.next();
		}
		return this.channel;
	}
	
	@SuppressWarnings("unchecked")
	private void createText(XmlPullParser parser) {
		String value = parser.getText().trim();
		if (StringUtil.isEmpty(value) || StringUtil.isEmpty(this.tag)) return;
		Map<String, Object> map = null;
		if (this.inside) {
			assert(this.item != null);
			map = this.createMap((Map<String, Object>)this.item.getInside(this.tag), createTextMap(StringUtil.removeTags(value)));
			this.item.addElement(this.tag, map);
		} else {
			assert(this.channel != null);
			map = this.createMap((Map<String, Object>)this.channel.getInside(this.tag), createTextMap(StringUtil.removeTags(value)));
			this.channel.addElement(this.tag, map);
		}
	}

	private Map<String, Object> createTextMap(String value) {
		Map<String, Object> textMap = new HashMap<String, Object>();
		textMap.put("text", value);
		return textMap;
	}

	private Map<String, Object> createMap(final Map<String, Object> itemMap, final Map<String, Object> textMap) {
		Map<String, Object> map = null;
		if (itemMap == null)  {
			map = new HashMap<String, Object>(textMap);
		} else {
			map = new HashMap<String, Object>(itemMap);
			if (!itemMap.containsKey("text")) map.putAll(textMap);
		}
		return map;
	}

	private void teminateElement(XmlPullParser parser) {
		String name = parser.getName();
		if (name.equals("channel")) {
			this.beginParse = false;
		} else if (name.equals("item")) {
			this.inside = false;
			this.channel.addItem(this.item);
		}
	}

	private void startElement(XmlPullParser parser) {
		String name = parser.getName();
		if (name.equals("channel")) {
			this.beginParse = true;
			this.channel = new Channel();
			return;
		} else if (name.equals("item")) {
			this.item = new Item();
			this.inside = true;
		}
		if (!this.beginParse) return;
		this.tag = name;
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			map.put(parser.getAttributeName(i), parser.getAttributeValue(i));
		}
		if (map.isEmpty()) return;
		if (this.inside) this.item.addElement(name, map);
		else this.channel.addElement(name, map);
	}

	private Reader createReader(final String urlStr) throws IOException {
		URL url = new URL(urlStr);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(url.openStream()));
		return buffReader;
	}
}
