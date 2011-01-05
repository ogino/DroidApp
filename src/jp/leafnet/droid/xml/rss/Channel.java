package jp.leafnet.droid.xml.rss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Channel {

	private List<Item> items;
	private Map<String, Object> map;

	public Channel() {
		this.items = new ArrayList<Item>();
		this.map = new HashMap<String, Object>();
	}

	public List<Item> getItems() {
		return items;
	}
	
	public void addItem(Item item) {
		this.items.add(item);
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public Object getInside(String key) {
		return this.map.get(key);
	}

	public void addMap(String key, Object value) {
		this.map.put(key, value);
	}
}
