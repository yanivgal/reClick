package com.reclick.reclick;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;
import unite.OnResponseListener;
import unite.Request;
import unite.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

public class MainActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	private LinearLayout mainLayout;
	private LinearLayout popUpLayout;
	private ImageButton settingsButton;
	private TextView helloUser;
	private EditText gameName;
	private EditText gameDescription;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mainLayout = (LinearLayout) findViewById(R.id.main_activity_main_layout);
		popUpLayout = (LinearLayout) findViewById(R.id.main_activity_create_game_popup_menu);
		settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		gameName = (EditText) findViewById(R.id.main_activity_create_game_popup_menu_game_name_editText);
		gameDescription = (EditText) findViewById(R.id.main_activity_create_game_popup_menu_game_description_editText);
		helloUser = (TextView) findViewById(R.id.main_activity_hello_user_textView);
		
		helloUser.setText(getString(R.string.main_activity_hello_user_textView_prefix_text)
						+ " " + Prefs.getNickname(this));
		
		((FrameLayout) findViewById(R.id.main_activity_container)).setOnTouchListener(onTouchListener);
		
		sendGetOpenGamesListRequest();
		sendGetCurrentUserGamesListRequest();
	}
	
	public void settingsButtonClicked(View view) {
		Prefs.removePref(this, Prefs.PROPERTY_USERNAME);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void createANewGameButtonClicked(View view) {
		showPopUpLayout();
	}
	
	public void createGameButtonClicked(View view) {
		Request request = (new Client()).post(Urls.createGame(this));
		request.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json));
		request.addParam("username", Prefs.getUsername(this));
		if (gameName != null && gameName.getText().toString() != null && !gameName.getText().toString().isEmpty()) {
			request.addParam("gameName", gameName.getText().toString());
		}
		if (gameDescription != null && gameDescription.getText().toString() != null && !gameDescription.getText().toString().isEmpty()) {
			request.addParam("gameDescription", gameDescription.getText().toString());
		}
		request.send(onCreateGameResponseListener);
	}
	
	private OnResponseListener onCreateGameResponseListener = new OnResponseListener() {
		public void onResponseReceived(Response response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				Log.e(TAG, response.getErrorMsg());
			}
			finish();
			startActivity(getIntent());
		}
	};
	
	@Override
	public void onBackPressed() {
		if (popUpLayout.getVisibility() == View.VISIBLE) {
			hidePopUpLayout();
		} else {
			super.onBackPressed();
		}
	}
	
	private void sendGetOpenGamesListRequest() {
		new Client()
			.get(Urls.getGames(this))
			.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json))
			.addParam("type", "open")
			.addParam("butUsername", Prefs.getUsername(this))
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
			.get(Urls.getUserGames(this, Prefs.getUsername(this)))
			.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json))
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
	
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			App.hideSoftKeyboard(MainActivity.this);
			return true;
		}
	};
	
	private void showPopUpLayout() {
		mainLayout.setVisibility(View.INVISIBLE);
		settingsButton.setVisibility(View.INVISIBLE);
		helloUser.setVisibility(View.INVISIBLE);
		popUpLayout.setVisibility(View.VISIBLE);
	}
	
	private void hidePopUpLayout() {
		popUpLayout.setVisibility(View.INVISIBLE);
		mainLayout.setVisibility(View.VISIBLE);
		settingsButton.setVisibility(View.VISIBLE);
		helloUser.setVisibility(View.VISIBLE);
	}
}