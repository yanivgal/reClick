package com.reclick.reclick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.protocol.HTTP;

import unite.Client;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

@SuppressLint("HandlerLeak")
public class GameActivity extends Activity {
	
	String gameId;
	String sequenceString;
	String correctStep;
	
	ArrayList<String> sequence;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			gameId = extras.getString("gameId");
			sequenceString = extras.getString("sequence");
		}
		
		setContentView(R.layout.game);
		
		if (sequenceString == null || sequenceString.equals("null")) {
			sequenceString = "";
			sequence = new ArrayList<String>();
		} else {
			sequence = new ArrayList<String>(Arrays.asList(sequenceString.split(",")));
		}
			
		if (!sequence.isEmpty()) {
			animateSequence(sequence, false);
		}
	}
	
	public void tileClicked(View view) {
		handleStep(view);
	}
	
	private void handleStep(View view) {
		int tileNum = Integer.parseInt((String) view.getTag());

		if (sequence.isEmpty()) {
			appendNewStep(tileNum);
			sendPlayerMove();
			finish();
			startActivity(new Intent(this, MainActivity.class));
			return;
		}
		
		if (!correctStep(tileNum)) {
			showCorrectStep();
		}
	}
	
	private void endGame() {
		new Client()
			.delete(Urls.deletePlayerFromGame(this, gameId, Prefs.getUsername(this)))
			.send();
		
		finish();
		startActivity(new Intent(this, MainActivity.class));
		return;
	}
	
	private void showCorrectStep() {
		ArrayList<String> correctTileSequence = new ArrayList<String>();
		int numOfBlinks = 5;
		for (int i = 0; i < numOfBlinks; i++) {
			correctTileSequence.add(correctStep);
		}
		animateSequence(correctTileSequence, true);
	}
	
	private void appendNewStep(int tileNum) {
		if (!sequenceString.isEmpty()) {
			sequenceString += ",";
		}
		sequenceString += tileNum;
	}
	
	private void sendPlayerMove() {
		new Client()
			.post(Urls.sendPlayerMove(this, gameId, Prefs.getUsername(this)))
			.setHeader(HTTP.CONTENT_TYPE, this.getString(R.string.application_json))
			.addParam("sequence", sequenceString)
			.send();
		
		App.showToast(this, "Nice Move");
	}
	
	private boolean correctStep(int tileNum) {
		correctStep = sequence.remove(0);
		return tileNum == Integer.parseInt(correctStep);
	}

	private void animateSequence(final ArrayList<String> sequence, final boolean endSequence) {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Executor executor = Executors.newSingleThreadExecutor();
				int i = 1;
				for (String tileNum : sequence) {
					if (i++ == sequence.size() && endSequence) {
						executor.execute(new TileAnimationRunnable(
								Integer.parseInt(tileNum), true));
					} else {
						executor.execute(new TileAnimationRunnable(
								Integer.parseInt(tileNum), false));
					}
				}
			}
		}, 1000);
	}
	
	private class TileAnimationRunnable implements Runnable {
		
		private int tileNum;
		private boolean lastTile;
		
		public TileAnimationRunnable(int tileNum, boolean lastTile) {
			this.tileNum = tileNum;
			this.lastTile = lastTile;
		}
		
		public void run() {
			if (Looper.myLooper() == null) {
		        Looper.prepare();
			}
			
			pressTileHandler.sendEmptyMessage(tileNum);
			
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) { }
			
			unpressTileHandler.sendEmptyMessage(tileNum);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
			
			if (lastTile) {
				endGame();
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
}