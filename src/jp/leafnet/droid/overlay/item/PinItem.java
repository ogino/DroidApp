package jp.leafnet.droid.overlay.item;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PinItem extends OverlayItem {

	public PinItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}
	
	public PinItem(GeoPoint point) {
		super(point, "", "");
	}

}
