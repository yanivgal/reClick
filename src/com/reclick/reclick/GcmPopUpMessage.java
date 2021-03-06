package com.reclick.reclick;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GcmPopUpMessage extends Activity {
	
	private Intent goToIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gcm_pop_up_message);
		
		Bundle extras = getIntent().getExtras();
		
		((TextView) findViewById(R.id.gcm_pop_up_message_text)).setText(extras.getString("message"));
		
		 // In case we need to go to MainActivity (Actually both are null in that case).
		if (!extras.containsKey("gameId") || !extras.containsKey("sequence")) {
			goToIntent = new Intent(this, MainActivity.class);
		} else {
			((Button) findViewById(R.id.gcm_pop_up_message_confirm_button)).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.gcm_pop_up_message_later_button)).setVisibility(View.VISIBLE);
			goToIntent = new Intent(this, GameActivity.class);
			goToIntent.putExtra("gameId", extras.getString("gameId"));
			goToIntent.putExtra("sequence", extras.getString("sequence"));
		}
	}
	
	public void confirmButtonClicked(View view) {
		removeGcmNotification();
		startActivity(goToIntent);
		finish();
	}
	
	public void laterButtonClicked(View view) {
		removeGcmNotification();
		finish();
	}
	
	private void removeGcmNotification() {
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
		.cancel(getResources()
		.getInteger(R.integer.gcm_notification_id));
	}
}