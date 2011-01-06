package jp.leafnet.droid.xml.handler;

import java.util.HashMap;
import java.util.Map;

import jp.leafnet.droid.util.StringUtil;
import jp.leafnet.droid.xml.rss.Channel;
import jp.leafnet.droid.xml.rss.Item;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RSSパーサのハンドラ
 * @author ogino
 * RSS2.0対応. RSS1.0には対応していない.
 */
public class RSSHandler extends DefaultHandler {

	private Boolean inside = false;
	private Boolean beginParse = false;
	private Channel channel = null;
	private Item item = null;
	private String tag = null;

	public Channel getChannel() {
		return this.channel;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (localName.equals("channel") || qName.equals("channel")) {
			this.beginParse = true;
			this.channel = new Channel();
			return;
		} else if ((localName.equals("item") || qName.equals("item")) && !this.inside) {
			this.item = new Item();
			this.inside = true;
			return;
		}

		String name = createName(localName, qName);
		if (this.beginParse && !StringUtil.isEmpty(name)) this.tag = name;
		else return;
		
		if (attributes == null) return;
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < attributes.getLength(); i++) {
			map.put(attributes.getLocalName(i), attributes.getValue(i));
		}
		if (map.isEmpty()) return;
		if (this.inside) this.item.addMap(name, map);
		else this.channel.addMap(name, map);
	}

	private String createName(final String localName, final String qName) {
		if (!StringUtil.isEmpty(localName))
			return localName;
		else if (!StringUtil.isEmpty(qName))
			return qName;
		return null;
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("channel") || qName.equals("channel")) {
			this.beginParse = false;
		} else if (localName.equals("item") || qName.equals("item")) {
			this.channel.addItem(this.item);
			this.inside = false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void characters(char[] ch, int start, int length) {
		if (!this.beginParse) return;
		String value = new String(ch, start, length).trim();
		if (value.length() < 1) return;
		Map<String, Object> map = null;
		if (this.inside) {
			assert(this.item != null);
			map = this.createMap((Map<String, Object>)this.item.getInside(this.tag), createTextMap(value));
			this.item.addMap(this.tag, map);
		} else {
			assert(this.channel != null);
			map = this.createMap((Map<String, Object>)this.channel.getInside(this.tag), createTextMap(value));
			this.channel.addMap(this.tag, map);
		}
		
	}

	private Map<String, Object> createTextMap(String value) {
		Map<String, Object> textMap = new HashMap<String, Object>();
		textMap.put("text", value);
		return textMap;
	}

	private Map<String, Object> createMap(Map<String, Object> itemMap, Map<String, Object> textMap) {
		if (itemMap == null) {
			return textMap;
		} else {
			itemMap.putAll(textMap);
			return itemMap;
		}
	}
}
