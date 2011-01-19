package jp.leafnet.droid.map;

import java.util.ResourceBundle;

import jp.leafnet.droid.R;
import jp.leafnet.droid.overlay.PinOverlay;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GoogleMap extends MapActivity implements LocationListener, GpsStatus.Listener {

	private LocationManager manager;
	private MapView mapView;
	private MapController controller;
	private PinOverlay overlay;
	private GeoPoint geoPoint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.createMapView();
		this.setContentView(this.mapView);
	}

	private void createMapView() {
		this.mapView = new MapView(this, this.createAPIKey());
		this.mapView.setEnabled(true);
		this.mapView.setClickable(true);
		this.mapView.setBuiltInZoomControls(true);
	}

	private String createAPIKey() {
		ResourceBundle bundle = ResourceBundle.getBundle("googlemap");
		if (bundle.getString("apikey.mode").equals("debug"))
			return bundle.getString("apikey.debug");
		else
			return bundle.getString("apikey.release");
	}

	@Override
	public void onStart() {
		super.onStart();
		this.createMapController();
		createLocationManager();
	}

	private void createLocationManager() {
		this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.manager.addGpsStatusListener(this);
		this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		this.manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}

	private void createMapController() {
		this.controller = this.mapView.getController();
		this.controller.setZoom(21);
	}

	private void createOverlay() {
		this.overlay = this.createPinOverlay();
		this.overlay.addPoint(this.geoPoint);
		this.mapView.getOverlays().add(this.overlay);
	}

	private PinOverlay createPinOverlay() {
		Drawable pin = this.getResources().getDrawable(R.drawable.pin);
		return new PinOverlay(pin);
	}

	@Override
	public void onStop() {
		super.onStop();
		this.mapView.clearFocus();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.manager.removeUpdates(this);
		this.manager.removeGpsStatusListener(this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected boolean isLocationDisplayed() {
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.geoPoint = new GeoPoint((int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6));
		if (this.overlay == null) createOverlay();
		createLocation();
	}

	private void createLocation() {
		this.overlay.clearPoint();
		this.overlay.addPoint(this.geoPoint);
		this.controller.setCenter(this.geoPoint);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
