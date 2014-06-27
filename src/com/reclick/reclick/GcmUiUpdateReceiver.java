package com.reclick.reclick;

import org.json.JSONException;
import org.json.JSONObject;

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

}
