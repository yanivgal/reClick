package com.reclick.reclick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;
import unite.OnResponseListener;
import unite.Response;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

@SuppressLint("HandlerLeak")
public class GameActivity extends Activity {
	
	private String gameId;
	private String sequenceString;
	private String correctStep;
	private ArrayList<String> sequence;
	private TextView gameMessage;
	private MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			gameId = extras.getString("gameId");
			sequenceString = extras.getString("sequence");
		}
		
		App.startLocationService(this);
		sendGetGamePlayersInfoRequest();
		
		setContentView(R.layout.game);
		
		gameMessage = (TextView) findViewById(R.id.game_game_message);
		
		if (sequenceString == null || sequenceString.equals("null")) {
			sequenceString = "";
			sequence = new ArrayList<String>();
		} else {
			sequence = new ArrayList<String>(Arrays.asList(sequenceString.split(",")));
		}
			
		if (!sequence.isEmpty()) {
			animateSequence(sequence, false, false);
		}
	}
	
	private void sendGetGamePlayersInfoRequest() {
		new Client()
			.get(Urls.getGame(this, gameId))
			.setHeader(HTTP.CONTENT_TYPE, getString(R.string.application_json))
			.send(onGamePlayersInfoResponseListener);
	}
	
	private OnResponseListener onGamePlayersInfoResponseListener = new OnResponseListener() {
		
		@Override
		public void onResponseReceived(Response response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				Log.e(App.getTag(GameActivity.this), response.getErrorMsg());
				return;
			}
			try {
				JSONObject jsonResponse = response.getJsonBody();
				if (jsonResponse.getString("status").equals("success")) {
					JSONArray players = jsonResponse.getJSONObject("data").getJSONObject("game").getJSONArray("players");
					RivalsAdapter rivalsAdapter = new RivalsAdapter(GameActivity.this, players);
					((ListView) findViewById(R.id.game_activity_rivals_info_list)).setAdapter(rivalsAdapter);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	public void tileClicked(View view) {
		handleStep(view);
	}
	
	private void handleStep(View view) {
		int tileNum = Integer.parseInt((String) view.getTag());

		if (sequence.isEmpty()) {
			String[] successMessages = getResources().getStringArray(R.array.game_activity_success_messages);
			String randomSuccessMessage = successMessages[new Random().nextInt(successMessages.length)];
			gameMessage.setText(randomSuccessMessage);
			gameMessage.setVisibility(View.VISIBLE);
			
			appendNewStep(tileNum);
			sendPlayerMove();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {}

					finish();
					startActivity(new Intent(GameActivity.this, MainActivity.class));
				}
			}).start();
			return;
		}
		
		if (!correctStep(tileNum)) {
			playMoveSound(0);
			showCorrectStep();
		} else {
			playMoveSound(tileNum);
		}
	}
	
	private Handler endGameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			new Client()
				.delete(Urls.deletePlayerFromGame(GameActivity.this, gameId, Prefs.getUsername(GameActivity.this)))
				.send();
			
			gameMessage.setText(getString(R.string.game_activity_game_over_message));
			gameMessage.setVisibility(View.VISIBLE);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {}

					finish();
					startActivity(new Intent(GameActivity.this, MainActivity.class));
				}
			}).start();
		};
	};
	
	private void showCorrectStep() {
		ArrayList<String> correctTileSequence = new ArrayList<String>();
		int numOfBlinks = 3;
		for (int i = 0; i < numOfBlinks; i++) {
			correctTileSequence.add(correctStep);
		}
		animateSequence(correctTileSequence, true, true);
	}
	
	private void appendNewStep(int tileNum) {
		if (!sequenceString.isEmpty()) {
			sequenceString += ",";
		}
		sequenceString += tileNum;
		playMoveSound(tileNum);
	}
	
	private void sendPlayerMove() {
		new Client()
			.post(Urls.sendPlayerMove(this, gameId, Prefs.getUsername(this)))
			.setHeader(HTTP.CONTENT_TYPE, this.getString(R.string.application_json))
			.addParam("sequence", sequenceString)
			.send();
	}
	
	private boolean correctStep(int tileNum) {
		correctStep = sequence.remove(0);
		return tileNum == Integer.parseInt(correctStep);
	}

	private void animateSequence(final ArrayList<String> sequence, final boolean endSequence, final boolean gameOver) {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Executor executor = Executors.newSingleThreadExecutor();
				int i = 1;
				for (String tileNum : sequence) {
					if (i++ == sequence.size() && endSequence) {
						executor.execute(new TileAnimationRunnable(
								Integer.parseInt(tileNum), true, gameOver));
					} else {
						executor.execute(new TileAnimationRunnable(
								Integer.parseInt(tileNum), false, gameOver));
					}
				}
			}
		}, 1000);
	}
	
	private class TileAnimationRunnable implements Runnable {
		
		private int tileNum;
		private boolean lastTile;
		private boolean gameOver;
		
		public TileAnimationRunnable(int tileNum, boolean lastTile, boolean gameOver) {
			this.tileNum = tileNum;
			this.lastTile = lastTile;
			this.gameOver = gameOver;
		}
		
		public void run() {
			if (Looper.myLooper() == null) {
		        Looper.prepare();
			}
			
			pressTileHandler.sendEmptyMessage(tileNum);
			
			if (!gameOver) {
				playMoveSound(tileNum);
			}
			
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {}
			
			unpressTileHandler.sendEmptyMessage(tileNum);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			
			if (lastTile) {
				endGameHandler.sendEmptyMessage(0);
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}
	
	private Handler unpressTileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			getTile(msg.what).setPressed(false);
		};
	};
	
	private Handler pressTileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			getTile(msg.what).setPressed(true);
		};
	};
	
	@SuppressLint("HandlerLeak")
	private ImageButton getTile(int tileNum) {
		int id = getResources().getIdentifier("tile_" + tileNum, "id", getPackageName());
		return (ImageButton) findViewById(id);
	}
	
	public void settingsButtonClicked(View view) {
		Prefs.removePref(this, Prefs.PROPERTY_USERNAME);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void playMoveSound(int tileNum) {
		
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
		
		if (tileNum == 1) {
			mediaPlayer = MediaPlayer.create(this, R.raw.blue);
			mediaPlayer.start();
		} else if (tileNum == 2) {
			mediaPlayer = MediaPlayer.create(this, R.raw.green);
			mediaPlayer.start();
		} else if (tileNum == 3) {
			mediaPlayer = MediaPlayer.create(this, R.raw.red);
			mediaPlayer.start();
		} else if (tileNum == 4) {
			mediaPlayer = MediaPlayer.create(this, R.raw.yellow);
			mediaPlayer.start();
		} else {
			mediaPlayer = MediaPlayer.create(this, R.raw.wrong);
			mediaPlayer.start();
		}
	}
	
//	@Override
//	protected void onUserLeaveHint() {
//		super.onUserLeaveHint();
//		finish();
//	}
	
	@Override
	protected void onPause() {
		super.onPause();
		App.stopLocationService(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		App.startLocationService(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.stopLocationService(this);
	}
}