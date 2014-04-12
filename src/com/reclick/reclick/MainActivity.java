package com.reclick.reclick;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.reclick.framework.Prefs;
import com.reclick.gcm.Gcm;
import com.reclick.request.Request;
import com.reclick.request.Request.RequestObject;

public class MainActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		if (checkPlayServices()) {
			new Gcm(this).register();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void login(View v) {
		RequestObject ro = new RequestObject("http://192.168.1.10/reclick/");
		JSONObject jsonObject = null;
		try {
			jsonObject = new Request(ro).execute().get();
		} catch (InterruptedException e) {
			
		} catch (ExecutionException e) {

		}
	}
	
	public void afterGcm() {
		String regId = Prefs.getGcmRegId(this);
		
		if (regId.isEmpty()) {
			Log.e(TAG, "Error creating GCM registration ID");
			finish();
		}
		
		Log.e(TAG, "GCM registration ID - " + regId);
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
