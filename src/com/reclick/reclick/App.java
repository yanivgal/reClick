package com.reclick.reclick;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class App {

	public static final String PROPERTY_APP_VERSION = "appVersion";

	public static int version(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	public static SharedPreferences preferences(Context context) {
		return context.getSharedPreferences(
				context.getString(R.string.app_name),
				Context.MODE_PRIVATE
				);
	}
	
	public static int getPrefAppVersion(Context context) {
		SharedPreferences prefs = preferences(context);
		return prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	}
}
