package com.reclick.features;

import org.apache.http.protocol.HTTP;

import com.reclick.framework.Prefs;
import com.reclick.reclick.R;
import com.reclick.request.Urls;

import unite.Client;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

public class LocationService extends Service implements LocationListener {
	
	private LocationManager locationManager = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!Prefs.getLocationServiceStatus(this)) {
			Prefs.setLocationServiceStatus(this, true);
			
			if (locationManager == null) {
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			}
			
			new Thread() {
				public void run() {
					Looper.prepare();
					getResources().getInteger(R.integer.min_time);
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							getResources().getInteger(R.integer.min_time),
							getResources().getInteger(R.integer.min_distance),
							LocationService.this
					);
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							getResources().getInteger(R.integer.min_time),
							getResources().getInteger(R.integer.min_distance),
							LocationService.this
					);
					Looper.loop();
				}
			}.start();
		}

		return START_STICKY;
	}

	@Override
	public void onLocationChanged(Location location) {

		if (location != null) {
			new Client()
				.post(Urls.setPlayerLocation(this, Prefs.getUsername(this)))
				.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json))
				.addParam("latitude", Double.toString(location.getLatitude()))
				.addParam("longitude", Double.toString(location.getLongitude()))
				.send();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {}

	@Override
	public void onProviderEnabled(String arg0) {}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (Prefs.getLocationServiceStatus(this)) {
			Prefs.setLocationServiceStatus(this, false);
			if (locationManager != null) {
				locationManager.removeUpdates(LocationService.this);
				locationManager = null;
			}
		}
	}
}