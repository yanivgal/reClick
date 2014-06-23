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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

@SuppressLint("HandlerLeak")
public class GameActivity extends Activity {
	
	String gameId;
	String sequenceString;
	
	ArrayList<String> sequence;
	
	ImageButton blueTile;
	ImageButton greenTile;
	ImageButton redTile;
	ImageButton yellowTile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			gameId = extras.getString("gameId");
			sequenceString = extras.getString("sequence");
		}
		if (gameId == null || sequenceString == null) {
			// TODO decide how to handle this
			App.showToast(this, "Can't instantiate game");
			finish();
			return;
		}
		
		setContentView(R.layout.game);
		
		sequence = new ArrayList<String>(Arrays.asList(sequenceString.split(",")));
		
//		blueTile = (ImageButton) findViewById(R.id.game_activity_blue_button);
//		greenTile = (ImageButton) findViewById(R.id.game_activity_green_button);
//		redTile = (ImageButton) findViewById(R.id.game_activity_red_button);
//		yellowTile = (ImageButton) findViewById(R.id.game_activity_yellow_button);

		animateLastSequence();	
	}
	
	public void blueButtonClicked(View view) {
		handleStep(view);
	}
	
	public void greenButtonClicked(View view) {
		handleStep(view);
	}
	
	public void redButtonClicked(View view) {
		handleStep(view);
	}
	
	public void yellowButtonClicked(View view) {
		handleStep(view);
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
			return;
		}
		
		if (!correctStep(tileNum)) {
			playerFailed();
			finish();
			return;
		}
	}
	
	private void appendNewStep(int tileNum) {
		sequenceString += "," + tileNum;
	}
	
	private void sendPlayerMove() {
		new Client()
			.post(Urls.sendPlayerMove(this, gameId, Prefs.getUsername(this)))
			.setHeader(HTTP.CONTENT_TYPE, this.getString(R.string.application_json))
			.addParam("sequence", sequenceString)
			.send();
		
		App.showToast(this, sequenceString);
	}
	
	private void playerFailed() {
		new Client()
			.delete(Urls.deletePlayerFromGame(this, gameId, Prefs.getUsername(this)))
			.send();
		
		App.showToast(this, "Fail");
	}
	
	private boolean correctStep(int tileNum) {
		int sequenceStep = Integer.parseInt(sequence.remove(0));
		return tileNum == sequenceStep;
	}
	
	public void signOut(View v) {
		Prefs.removePref(this, Prefs.PROPERTY_USERNAME);
		Intent intent = new Intent(this, com.reclick.reclick.LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	

	private void animateLastSequence() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Executor executor = Executors.newSingleThreadExecutor();
				for (String tileNum : sequence) {
					executor.execute(new TileAnimationRunnable(Integer
							.parseInt(tileNum)));
				}
			}
		}, 2000);
	}
	
	private class TileAnimationRunnable implements Runnable {
		
		private int tileNum;
		
		public TileAnimationRunnable(int tileNum) {
			this.tileNum = tileNum;
		}
		
		public void run() {
			pressTileHandler.sendEmptyMessage(tileNum);
			
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) { }
			
			unpressTileHandler.sendEmptyMessage(tileNum);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
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
}