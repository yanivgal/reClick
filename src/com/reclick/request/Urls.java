package com.reclick.request;

import java.io.File;

import com.reclick.reclick.R;

import android.content.Context;

public class Urls {

	public static String login(Context context) {
		return buildUrl(context, context.getString(R.string.method_login));
	}
	
	public static String signup(Context context) {
		return buildUrl(context, context.getString(R.string.method_sign_up));
	}
	
	public static String getGames(Context context) {
		return buildUrl(context, context.getString(R.string.games_collection));
	}
	
	public static String addPlayerToGame(Context context, String gameId, String username) {
		return buildUrl(context,
						context.getString(R.string.games_collection)
							+ File.separator
							+ gameId
							+ File.separator
							+ context.getString(R.string.players_collection)
							+ File.separator
							+ username
						);
	}
	
	private static String buildUrl(Context context, String pathSuffix) {
		return context.getString(R.string.scheme)
				+ File.pathSeparator
				+ File.separator
				+ File.separator
				+ context.getString(R.string.host)
				+ File.separator
				+ context.getString(R.string.path_prefix)
				+ File.separator
				+ pathSuffix;
	}
}