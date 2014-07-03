package com.reclick.reclick;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;
import unite.OnResponseListener;
import unite.Response;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class GcmUiUpdateReceiver extends BroadcastReceiver {
	
	public static final String ACTION_GAME_CREATED = "gameCreated";
	public static final String ACTION_GAME_CREATED_CREATOR = "gameCreatedCreator";
	public static final String ACTION_UPDATE_GAMES = "updateGames";
	
	private Activity activity;
	
	public GcmUiUpdateReceiver(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		if (action.equals(ACTION_GAME_CREATED)) {
			gameCreated(intent);
		} else if (action.equals(ACTION_GAME_CREATED_CREATOR)) {
			gameCreatedCreator(intent);
		} else if (action.equals(ACTION_UPDATE_GAMES)) {
			updateGames();
		}
	}
	
	private void gameCreated(Intent intent) {
		Bundle extras = intent.getExtras();
		ListView openGamesList = (ListView) activity.findViewById(R.id.main_activity_open_games_list);
		GamesAdapter openGamesListAdapter = (GamesAdapter) openGamesList.getAdapter();
		JSONObject game = new JSONObject();
		try {
			game.put("id", extras.getString("id"));
			game.put("name", extras.getString("name"));
			game.put("description", extras.getString("description"));
			game.put("sequence", extras.getString("sequence"));
			game.put("started", extras.getString("started"));
		} catch (JSONException e) {
			return;
		}
		openGamesListAdapter.add(game);
	}
	
	private void gameCreatedCreator(Intent intent) {
		Bundle extras = intent.getExtras();
		ListView currentUserGamesList = (ListView) activity.findViewById(R.id.main_activity_current_user_games_list);
		GamesAdapter currentUserGamesListAdapter = (GamesAdapter) currentUserGamesList.getAdapter();
		JSONObject game = new JSONObject();
		try {
			game.put("id", extras.getString("id"));
			game.put("name", extras.getString("name"));
			game.put("description", extras.getString("description"));
			game.put("sequence", extras.getString("sequence"));
			game.put("started", extras.getString("started"));
		} catch (JSONException e) {
			return;
		}
		currentUserGamesListAdapter.add(game);
	}
	
	private void updateGames() {
		new Client()
			.get(Urls.getUserGames(activity, Prefs.getUsername(activity)))
			.setHeader(HTTP.CONTENT_TYPE, activity.getString(R.string.application_json))
			.send(onCurrUserGamesResponseListener);
	}
	
	private OnResponseListener onCurrUserGamesResponseListener = new OnResponseListener() {
		
		@Override
		public void onResponseReceived(Response response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				Log.e(App.getTag(activity), response.getErrorMsg());
				return;
			}
			try {
				JSONObject jsonResponse = response.getJsonBody();
				if (jsonResponse.getString("status").equals("success")) {
					JSONArray games = jsonResponse.getJSONObject("data").getJSONArray("games");
					ListView currentUserGamesList = (ListView) activity.findViewById(R.id.main_activity_current_user_games_list);
					GamesAdapter currentUserGamesListAdapter = (GamesAdapter) currentUserGamesList.getAdapter();
					currentUserGamesListAdapter.updateGames(games);
				} else {
					App.showToast(activity, jsonResponse.getString("message"));
				}
			} catch (JSONException e) { }
		}
	};

}
