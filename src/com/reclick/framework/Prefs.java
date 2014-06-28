package com.reclick.framework;

import com.reclick.reclick.R;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_GCM_REG_ID = "gcmRegistrationId";
	public static final String PROPERTY_USERNAME = "username";
	public static final String PROPERTY_NICKNAME = "nickname";
	public static final String PROPERTY_LOCATION_SERVICE_ON = "isLocationServiceOn";
	
	public static SharedPreferences preferences(Context context) {
		return context.getSharedPreferences(
					context.getString(R.string.app_name),
					Context.MODE_PRIVATE
				);
	}
	
	public static SharedPreferences.Editor preferencesEditor(Context context) {
		return preferences(context).edit();
	}
	
	public static int getAppVersion(Context context) {
		return (Integer) get(context, PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	}
	
	public static void setAppVersion(Context context, int value) {
		set(context, PROPERTY_APP_VERSION, value);
	}
	
	public static String getGcmRegId(Context context) {
		return (String) get(context, PROPERTY_GCM_REG_ID, "");
	}
	
	public static void setGcmRegId(Context context, String value) {
		set(context, PROPERTY_GCM_REG_ID, value);
	}
	
	public static String getUsername(Context context) {
		return (String) get(context, PROPERTY_USERNAME, "");
	}
	
	public static void setUsername(Context context, String value) {
		set(context, PROPERTY_USERNAME, value);
	}
	
	public static boolean getLocationServiceStatus(Context context) {
		return (Boolean) get(context, PROPERTY_LOCATION_SERVICE_ON, false);
	}
	
	public static void setLocationServiceStatus(Context context, boolean value) {
		set(context, PROPERTY_LOCATION_SERVICE_ON, value);
	}
	
	public static String getNickname(Context context) {
		return (String) get(context, PROPERTY_NICKNAME, "");
	}
	
	public static void setNickname(Context context, String value) {
		set(context, PROPERTY_NICKNAME, value);
	}
	
	public static void removePref(Context context, String key) {
		SharedPreferences.Editor editor= preferencesEditor(context);
		editor.remove(key);
		editor.commit();
	}
	
	private static Object get(Context context, String key, Object defaultValue) {
		SharedPreferences prefs = preferences(context);
		if (defaultValue instanceof Integer) {
			return prefs.getInt(key, (Integer) defaultValue);
		}
		if (defaultValue instanceof Float) {
			return prefs.getFloat(key, (Float) defaultValue);
		}
		if (defaultValue instanceof String) {
			return prefs.getString(key, (String) defaultValue);
		}
		if (defaultValue instanceof Boolean) {
			return prefs.getBoolean(key, (Boolean) defaultValue);
		}
		return null;
	}
	
	private static void set(Context context, String key, Object value) {
		SharedPreferences.Editor prefsEditor = preferencesEditor(context);
		if (value instanceof Integer) {
			prefsEditor.putInt(key, (Integer) value);
		} else if (value instanceof Float) {
			prefsEditor.putFloat(key, (Float) value);
		} else if (value instanceof String) {
			prefsEditor.putString(key, (String) value);
		} else if (value instanceof Boolean) {
			prefsEditor.putBoolean(key, (Boolean) value);
		}
		prefsEditor.commit();
	}
}
