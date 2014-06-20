package com.reclick.request;

import com.reclick.reclick.R;

import android.content.Context;

public class Urls {
	
	private static final String LOGIN = "method_login";
	private static final String SIGN_UP = "method_sign_up";

	public static String login(Context context) {
		return buildUrl(context, LOGIN);
	}
	
	public static String signup(Context context) {
		return buildUrl(context, SIGN_UP);
	}
	
	private static String buildUrl(Context context, String method) {
		return context.getString(R.string.scheme)
				+ "://"
				+ context.getString(R.string.host)
				+ "/"
				+ context.getString(R.string.path)
				+ "/"
				+ context.getString(getStringIdentifier(context, method));
	}
	
	private static int getStringIdentifier(Context context, String name) {
	    return context.getResources().getIdentifier(
	    		name, "string", context.getPackageName());
	}
}