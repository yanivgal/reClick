package com.reclick.framework;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class App {
	
	private final static String TAG = "App";
	
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
	
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	public static final String md5(final String s) {
	    final String MD5 = "MD5";
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance(MD5);
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        Log.e(TAG, e.getMessage());
	    }
	    return "";
	}
	
	public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
        		.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
        	imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
	}
	
	public static String getTag(Context context) {
		return context.getClass().getSimpleName();
	}
}