package com.reclick.reclick;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.reclick.framework.Prefs;
import com.reclick.gcm.Gcm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		if (checkPlayServices()) {
			new Gcm(this).register();
		}
	}
	
	public void afterGcm() {		
		if (Prefs.getGcmRegId(this).isEmpty()) {
			Log.e(TAG, "Error creating GCM registration ID");
			finish();
		}
		
		Intent intent;
		
		if (Prefs.getUsername(this).isEmpty()) {
			intent = new Intent(this, com.reclick.reclick.LoginActivity.class);
		} else {
			intent = new Intent(this, com.reclick.reclick.MainActivity.class);
		}
		
		startActivity(intent);
		
		finish();
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    9000).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
}
