package com.reclick.reclick;

import java.util.ArrayList;
import java.util.Arrays;

import unite.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

public class GameActivity extends Activity {
	
	String gameId;
	String sequenceString;
	
	ArrayList<String> sequence;
	
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
		String s = new Client()
			.post(Urls.sendPlayerMove(this, gameId, Prefs.getUsername(this)))
				.setBody("{\"sequence\":\"" + sequenceString + "\"}")
				.send()
				.getBody();
		
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
}