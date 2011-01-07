package jp.leafnet.droid.xml.rss;

import java.util.HashMap;
import java.util.Map;

public class Item {
	private Map<String, Object> elementMap;

	public Item() {
		this.elementMap = new HashMap<String, Object>();
	}
	
	public Object getInside(String key) {
		return this.elementMap.get(key);
	}

	public void addElement(String key, Object value) {
		this.elementMap.put(key, value);
	}
}
