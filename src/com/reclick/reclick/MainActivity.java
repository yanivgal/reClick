package com.reclick.reclick;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
}
