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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

public class MainActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	private LinearLayout mainLayout;
	private LinearLayout popUpLayout;
	private ImageButton settingsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mainLayout = (LinearLayout) findViewById(R.id.main_activity_main_layout);
		popUpLayout = (LinearLayout) findViewById(R.id.main_activity_create_game_popup_menu);
		settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		
		sendGetOpenGamesListRequest();
		sendGetCurrentUserGamesListRequest();
	}
	
	public void settingsButtonClicked(View view) {
		Prefs.removePref(this, Prefs.PROPERTY_USERNAME);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void createGameButtonClicked(View view) {
		mainLayout.setVisibility(View.INVISIBLE);
		settingsButton.setVisibility(View.INVISIBLE);
		popUpLayout.setVisibility(View.VISIBLE);
	}
	
	public void createGame(View view) {
//		new Client()
//			.
	}
	
	public void invite(View view) {
		
	}
	
	@Override
	public void onBackPressed() {
		if (popUpLayout.getVisibility() == View.VISIBLE) {
			popUpLayout.setVisibility(View.INVISIBLE);
			mainLayout.setVisibility(View.VISIBLE);
			settingsButton.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
		}
	}
	
	private void sendGetOpenGamesListRequest() {
		new Client()
			.get(Urls.getGames(this))
			.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json))
			.send(onOpenGamesResponseListener);
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
			.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json))
			.addParam("username", Prefs.getUsername(this))
			.send(onCurrUserGamesResponseListener);
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