package jp.leafnet.droid.xml.rss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Channel {

	private List<Item> itemList;
	private Map<String, Object> elementMap;

	public Channel() {
		this.itemList = new ArrayList<Item>();
		this.elementMap = new HashMap<String, Object>();
	}

	public List<Item> getItems() {
		return itemList;
	}
	
	public void addItem(Item item) {
		this.itemList.add(item);
	}

	public Object getInside(String key) {
		return this.elementMap.get(key);
	}

	public void addElement(String key, Object value) {
		this.elementMap.put(key, value);
	}
}
