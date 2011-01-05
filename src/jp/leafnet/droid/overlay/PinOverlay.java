package jp.leafnet.droid.overlay;

import java.util.ArrayList;
import java.util.List;

import jp.leafnet.droid.overlay.item.PinItem;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;

public class PinOverlay extends ItemizedOverlay<PinItem> {

	private List<GeoPoint> points = new ArrayList<GeoPoint>();

	public PinOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}

	@Override
	protected PinItem createItem(int i) {
		GeoPoint point = this.points.get(i);
		return new PinItem(point);
	}

	@Override
	public int size() {
		return this.points.size();
	}

	public void addPoint(GeoPoint point) {
		this.points.add(point);
		populate();
	}

	public void clearPoint() {
		this.points.clear();
		populate();
	}

}
