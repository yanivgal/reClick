package com.reclick.reclick;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class LoginActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}
}
