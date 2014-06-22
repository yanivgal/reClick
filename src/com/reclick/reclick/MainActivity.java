package com.reclick.reclick;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;
import unite.OnResponseListener;
import unite.Response;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

public class MainActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		sendGetOpenGamesListRequest();
		sendGetCurrentUserGamesListRequest();
	}
	
	public void createGameButtonClicked(View view) {
//		((LinearLayout) findViewById(R.id.main_create_new_game_popup_window)).setVisibility(View.VISIBLE);
		App.showToast(this, "Create button has been pressed");
	}
	
	private void sendGetOpenGamesListRequest() {
		new Client()
			.get(Urls.getGames(this))
			.setHeader(HTTP.CONTENT_TYPE, "application/json")
			.setOnResponseListener(onOpenGamesResponseListener)
			.send();
	}
	
	private OnResponseListener onOpenGamesResponseListener = new OnResponseListener() {
		
		@Override
		public void onResponseReceived(Response response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				Log.e(TAG, response.getErrorMsg());
				return;
			}
			try {
				JSONObject jsonResponse = response.getJsonBody();
				if (jsonResponse.getString("status").equals("success")) {
					JSONArray games = jsonResponse.getJSONObject("data").getJSONArray("games");
					GamesAdapter openGamesAdapter = new GamesAdapter(MainActivity.this, games, false);
					((ListView) findViewById(R.id.main_activity_open_games_list)).setAdapter(openGamesAdapter);
				} else {
					App.showToast(MainActivity.this, jsonResponse.getString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	
	private void sendGetCurrentUserGamesListRequest() {
		new Client()
			.get(Urls.getGames(this))
			.setHeader(HTTP.CONTENT_TYPE, "application/json")
			.addParam("username", Prefs.getUsername(this))
			.setOnResponseListener(onCurrUserGamesResponseListener)
			.send();
	}
	
	private OnResponseListener onCurrUserGamesResponseListener = new OnResponseListener() {
		
		@Override
		public void onResponseReceived(Response response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				Log.e(TAG, response.getErrorMsg());
				return;
			}
			try {
				JSONObject jsonResponse = response.getJsonBody();
				if (jsonResponse.getString("status").equals("success")) {
					JSONArray games = jsonResponse.getJSONObject("data").getJSONArray("games");
					GamesAdapter currentUserGamesAdapter = new GamesAdapter(MainActivity.this, games, true);
					((ListView) findViewById(R.id.main_activity_current_user_games_list)).setAdapter(currentUserGamesAdapter);
				} else {
					App.showToast(MainActivity.this, jsonResponse.getString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
}