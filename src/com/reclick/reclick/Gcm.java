package com.reclick.reclick;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.reclick.R;

public class Gcm {
	
	private Context context;
	
	public Gcm(Context context) {
		this.context = context;
	}
	
	public void register() {
		new GcmRegister(context).execute();
	}

	private class GcmRegister extends AsyncTask<Void, Void, Void> {
		
		private final String TAG = this.getClass().getSimpleName();
		private final int NUM_OF_GCM_RETRIES = 5;
		
		private Context context;
		private SplashActivity splashActivity;
		
		public GcmRegister(Context context) {
			super();
			this.context = context;
			this.splashActivity = (SplashActivity) context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			String regId = getRegistrationId(context);
			if (regId.isEmpty()) {
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
				String exceptionMessage;
				int numOfTries = 0;
				do {
					exceptionMessage = "";
					numOfTries++;
					
					try {
						regId = gcm.register(
								context.getString(R.string.gcm_sender_id));
						Prefs.setGcmRegId(context, regId);
						Prefs.setAppVersion(context, App.version(context));
					} catch (IOException e) {
						exceptionMessage = e.getMessage();
						Log.e(TAG, "Try #" + numOfTries + " - " + e.getMessage());
					}
					/*
					 * Per Google GCM documentation:
					 * When the application receives a
					 * com.google.android.c2dm.intent.REGISTRATION intent with
					 * the error extra set as SERVICE_NOT_AVAILABLE, it should
					 * retry the failed operation (register or unregister).
					 */
				} while(exceptionMessage.equals(
						GoogleCloudMessaging.ERROR_SERVICE_NOT_AVAILABLE) &&
						numOfTries < NUM_OF_GCM_RETRIES);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			splashActivity.afterGcm();
		}
		
		private String getRegistrationId(Context context) {
			String regId = Prefs.getGcmRegId(context);
			
			// Check if app was updated; if so, it must clear the registration ID
		    // since the existing regID is not guaranteed to work with the new
		    // app version.
		    int registeredVersion = Prefs.getAppVersion(context);
		    int currentVersion = App.version(context);
		    if (registeredVersion != currentVersion) {
		        return "";
		    }
			
			return regId;
		}		
	}
}
