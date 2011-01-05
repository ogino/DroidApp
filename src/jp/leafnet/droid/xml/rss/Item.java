package jp.leafnet.droid.xml.rss;

import java.util.HashMap;
import java.util.Map;

public class Item {
	private Map<String, Object> map;

	public Item() {
		this.map = new HashMap<String, Object>();
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
