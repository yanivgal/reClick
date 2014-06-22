package com.reclick.reclick;

import com.reclick.framework.Prefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class GameActivity extends Activity {
	
//	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
	}
	
	public void blueButtonClicked(View view) {
		Toast.makeText(this, "Blue button has been pressed", Toast.LENGTH_SHORT).show();
	}
	
	public void greenButtonClicked(View view) {
		Toast.makeText(this, "Green button has been pressed", Toast.LENGTH_SHORT).show();
	}
	
	public void redButtonClicked(View view) {
		Toast.makeText(this, "Red button has been pressed", Toast.LENGTH_SHORT).show();
	}
	
	public void yellowButtonClicked(View view) {
		Toast.makeText(this, "Yellow button has been pressed", Toast.LENGTH_SHORT).show();
	}
	
	public void signOut(View v) {
		Prefs.removePref(this, Prefs.PROPERTY_USERNAME);
		Intent intent = new Intent(this, com.reclick.reclick.LoginActivity.class);
		startActivity(intent);
		finish();
	}
}