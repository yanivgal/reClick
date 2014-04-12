package com.reclick.framework;

import android.content.Context;
import android.content.SharedPreferences;
import com.reclick.reclick.R;

public class Prefs {
	
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_GCM_REG_ID = "gcmRegistrationId";
	
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
